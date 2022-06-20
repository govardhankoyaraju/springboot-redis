package com.vardhan.springbootredis.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisDao {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveData(String key, Map<String, Object> cacheDataMap) {
        log.info("Saving the data with key={} & data={}", key, cacheDataMap);
        try {
            redisTemplate.opsForValue().set(key, cacheDataMap);
        } catch (Exception ex) {
            log.error("Error occurred while saving the data. [key={}, data={}]", key, cacheDataMap);
            throw new RuntimeException("Error occurred while saving the data");
        }
    }

    public Map<String, Object> getData(String key) {
        log.info("Get the redis cache data with key={}", key);
        Map<String, Object> resultMap;
        try {
            resultMap = (Map<String, Object>) redisTemplate.opsForValue().get(key);
        } catch (Exception ex) {
            log.error("Error occurred while retrieving the data. [key={}]", key);
            throw new RuntimeException("Error occurred while retrieving the data");
        }
        return resultMap;
    }

    public Boolean deleteData(String key) {
        log.info("Delete the redis cache data with key={}", key);
        Boolean isDeleted;
        try {
            isDeleted = redisTemplate.delete(key);
        } catch (Exception ex) {
            log.error("Error occurred while deleting the data. [key={}]", key);
            throw new RuntimeException("Error occurred while deleting the data");
        }
        return isDeleted;
    }
}
