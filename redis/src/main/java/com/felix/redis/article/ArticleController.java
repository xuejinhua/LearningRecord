package com.felix.redis.article;

import com.felix.redis.utils.RedisUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.felix.redis.article.ArticleConstant.*;

@Slf4j
@RestController
@RequestMapping("article")
@AllArgsConstructor
public class ArticleController {

    private final RedisUtils redisUtils;

    /**
     * 投票方法
     *
     * @param user
     * @param article
     */
    @RequestMapping("articleVote")
    public void articleVote(@RequestParam String user, @RequestParam String article) {
        // 当前时间减去一周
        Long cutOff = System.currentTimeMillis() - ONE_WEEK_IN_SECONDS;
        // 文章发布的时间+获得的分数 < cutOff : 发布时间过早并且投票分数不高->不能在投票
        Long score = redisUtils.zScore(PREFIX_TIME, article).longValue();
        if (score == null || score < cutOff) {
            return;
        }
        // 获取文章的id
        String articleId = article.split(":")[1];
        // 判断有没有投过赞成票
        Boolean exists = redisUtils.exists(PREFIX_NEGATIVE_VOTE + articleId);
        if (exists) {
            redisUtils.sRemove(PREFIX_NEGATIVE_VOTE + articleId, user);
        }
        // 判断用户是否已经为该文章投过票
        Boolean add = redisUtils.sAdd(PREFIX_VOTE + articleId, user);
        if (add) {
            // 文章的分数增加
            redisUtils.zIncrementScore(PREFIX_SCORE, article, VOTE_SCORE);
            // 文章的投票增加
            redisUtils.hIncrement(article, VOTES, 1.0);
        }
    }

    /**
     * 投反对票方法
     *
     * @param user
     * @param article
     */
    @RequestMapping("articleNegativeVote")
    public void articleNegativeVote(@RequestParam String user, @RequestParam String article) {
        // 当前时间减去一周
        Long cutOff = System.currentTimeMillis() - ONE_WEEK_IN_SECONDS;
        // 文章发布的时间+获得的分数 < cutOff : 发布时间过早并且投票分数不高->不能在投票
        Double score = redisUtils.zScore(PREFIX_TIME, article);
        if (score == null || score < cutOff) {
            return;
        }
        // 获取文章的id
        String articleId = article.split(":")[1];
        // 判断有没有投过赞成票
        Boolean exists = redisUtils.exists(PREFIX_VOTE + articleId);
        if (exists) {
            redisUtils.sRemove(PREFIX_VOTE + articleId, user);
        }
        // 判断用户是否已经为该文章投过票
        Boolean add = redisUtils.sAdd(PREFIX_NEGATIVE_VOTE + articleId, user);
        if (add) {
            // 文章的分数增加
            redisUtils.zIncrementScore(PREFIX_SCORE, article, -VOTE_SCORE);
            // 文章的投票增加
            redisUtils.hIncrement(article, VOTES, -1.0);
        }
    }

    /**
     * 创建文章
     *
     * @param user
     * @param title
     * @param link
     * @return
     */
    @RequestMapping("postArticle")
    public String postArticle(@RequestParam String user, @RequestParam String title, @RequestParam String link) {
        // 生产文章id
        String articleId = String.valueOf(redisUtils.increment(PREFIX_ARTICLE));
        String voted = PREFIX_VOTE + articleId;
        // 将文章的发布用户添加到文章里面
        redisUtils.sAdd(voted, user);
        // 设置文章的过期时间为一周
        redisUtils.expire(voted, ONE_WEEK_IN_SECONDS, TimeUnit.MILLISECONDS);
        long now = System.currentTimeMillis();
        String article = PREFIX_ARTICLE + articleId;
        Map<String, String> articleMap = new HashMap<>();
        articleMap.put("title", title);
        articleMap.put("link", link);
        articleMap.put("poster", user);
        articleMap.put("time", String.valueOf(now));
        articleMap.put("vote", String.valueOf(1));
        redisUtils.putAll(article, articleMap);
        // 将文章添加到根据评分排序的有序集合，创建人默认投一票
        redisUtils.zAdd(PREFIX_SCORE, article, now + VOTE_SCORE);
        // 将文章添加到根据发布时间排序的有序集合和
        redisUtils.zAdd(PREFIX_TIME, article, now);
        return articleId;
    }

    /**
     * 文章获取功能
     *
     * @param page
     * @return
     */
    @RequestMapping("getArticles")
    public List<Map<Object, Object>> getArticles(@RequestParam Integer page, @RequestParam String order) {
        List<Map<Object, Object>> articles = new ArrayList<>();
        Integer start = (page - 1) * ARTICLES_PER_PAGE;
        Integer end = start + ARTICLES_PER_PAGE - 1;
        Set<Object> ids = redisUtils.zRange(order, start, end);
        for (Object id : ids) {
            Map<Object, Object> articleMap = redisUtils.getAll(String.valueOf(id));
            articleMap.put("id", id);
            articles.add(articleMap);
        }
        return articles;
    }

    /**
     * 将文章添加到分组
     *
     * @param articleId
     * @param toAdd
     */
    @RequestMapping("addGroup")
    public void addGroup(@RequestParam String articleId, @RequestParam List<String> toAdd) {
        String article = PREFIX_ARTICLE + articleId;
        for (String group : toAdd) {
            redisUtils.sAdd(PREFIX_GROUP + group, article);
        }
    }

    /**
     * 将文章移除分组 ·
     *
     * @param articleId
     * @param toRemove
     */
    @RequestMapping("removeGroup")
    public void removeGroup(@RequestParam String articleId, @RequestParam List<String> toRemove) {
        String article = PREFIX_ARTICLE + articleId;
        for (String group : toRemove) {
            redisUtils.sRemove(PREFIX_GROUP + group, article);
        }
    }

    /**
     * 文章分组获取功能
     *
     * @param page
     * @return
     */
    @RequestMapping("getGroupArticles")
    public List getGroupArticles(@RequestParam String group, @RequestParam Integer page, @RequestParam String order) {
        String key = order + group;
        if (!redisUtils.exists(key)) {
            List<String> otherKeys = new ArrayList<>();
            otherKeys.add(order);
            // 将分组文章和文章分数进行交集排序
            redisUtils.intersectAndStore(PREFIX_GROUP + group, otherKeys, key, RedisZSetCommands.Aggregate.MAX);
            redisUtils.expire(key, 60, TimeUnit.SECONDS);
        }
        return getArticles(page, key);
    }


}
