package com.felix.redis.article;

public class ArticleConstant {
    public final static Long ONE_WEEK_IN_SECONDS = 7 * 24 * 60 * 60L;
    public final static Double VOTE_SCORE = 432D;
    public final static String PREFIX_TIME = "time:";
    public final static String PREFIX_VOTE = "vote:";
    public final static String PREFIX_NEGATIVE_VOTE = "negative_vote:";
    public final static String PREFIX_SCORE = "score:";
    public final static String VOTES = "votes";
    public final static String PREFIX_ARTICLE = "article:";
    public final static String PREFIX_GROUP = "group:";
    public final static Integer ARTICLES_PER_PAGE = 25;
}
