package com.felix.redis.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.felix.redis.article.ArticleConstant.PREFIX_TIME;

@Slf4j
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Double zScore(String key, Object o) {
        try {
            return redisTemplate.opsForZSet().score(PREFIX_TIME, o);
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean sAdd(String key, Object o) {
        try {
            Long add = redisTemplate.opsForSet().add(key, o);
            return add != null && add > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public void zIncrementScore(String key, Object o, Double delta) {
        try {
            redisTemplate.opsForZSet().incrementScore(key, o, delta);
        } catch (Exception ex) {
            log.error("zIncrementScore failure!", ex);
        }
    }

    public void hIncrement(String key, Object hashKey, Double delta) {
        try {
            redisTemplate.opsForHash().increment(key, hashKey, delta);
        } catch (Exception ex) {
            log.error("hIncrement failure!", ex);
        }
    }

    public Long increment(String key) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (Exception ex) {
            log.error("increment failure!", ex);
            return null;
        }
    }

    public Boolean expire(String key, long timeout, TimeUnit timeUnit) {
        try {
            return redisTemplate.expire(key, timeout, timeUnit);
        } catch (Exception ex) {
            log.error("expire failure!", ex);
            return null;
        }
    }

    public Boolean zAdd(String key, Object o, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, o, score);
        } catch (Exception ex) {
            log.error("zAdd failure!", ex);
            return false;
        }
    }

    public void putAll(String key, Map<String, String> articleMap) {
        try {
            redisTemplate.opsForHash().putAll(key, articleMap);
        } catch (Exception ex) {
            log.error("zAdd failure!", ex);
        }
    }

    public Set<Object> zRange(String key, Integer start, Integer end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception ex) {
            log.error("zRange failure!", ex);
            return new HashSet<>();
        }
    }

    public Map<Object, Object> getAll(String id) {
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(id);
            return entries;
        } catch (Exception ex) {
            log.error("zRange failure!", ex);
            return new HashMap<>();
        }
    }

    public Boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception ex) {
            log.error("zRange failure!", ex);
            return false;
        }
    }

    public Long sRemove(String key, Object o) {
        try {
            return redisTemplate.opsForSet().remove(key, o);
        } catch (Exception ex) {
            log.error("sRemove failure!", ex);
            return null;
        }
    }

    public Long intersectAndStore(String key, List<String> otherKeys, String destKey, RedisZSetCommands.Aggregate max) {
        try {
            return redisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey, max);
        } catch (Exception ex) {
            log.error("sRemove failure!", ex);
            return null;
        }
    }
}
