package com.gym.Elite.Gym.integration.queue;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

    private final StringRedisTemplate redisTemplate;

    public QueueService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void push(String queue, String data) {
        redisTemplate.opsForList().leftPush(queue, data);
    }

    public String pop(String queue) {
        return redisTemplate.opsForList().rightPop(queue);
    }
}
