package com.github.sentrionic.olympusblog.service;

import com.github.sentrionic.olympusblog.config.AppProperties;
import com.github.sentrionic.olympusblog.exception.BadTokenException;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RedisService {
    private final RedisCommands<String, String> redis;

    public RedisService(AppProperties appProperties) {
        var connectionUrl = String.format("redis://%s:%s", appProperties.getRedisUrl(), appProperties.getRedisPort());
        redis = RedisClient.create(connectionUrl).connect().sync();
    }

    public void saveUserId(String id, UUID token) {
        redis.set(getKey(token.toString()), id, new SetArgs().ex(1000 * 60 * 60 * 24 * 3));
    }

    public Long getUserId(String token) {
        var value = redis.get(getKey(token));
        if (value == null) {
            throw new BadTokenException("Token Expired");
        }
        return Long.parseLong(value);
    }

    public void deleteKey(String token) {
        redis.del(getKey(token));
    }

    private String getKey(String token) {
        return "forget-password:" + token;
    }
}
