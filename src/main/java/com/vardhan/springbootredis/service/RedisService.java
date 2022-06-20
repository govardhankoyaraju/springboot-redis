package com.vardhan.springbootredis.service;

import com.vardhan.springbootredis.dao.RedisDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final RedisDao redisDao;
    public void saveData(String key, Map<String, Object> cacheDataMap) {
        log.info("Entered RedisService to method saveData with key={} & data={}", key, cacheDataMap);
        redisDao.saveData(key, cacheDataMap);
    }

    public Map<String, Object> getData(String key) {
        log.info("Entered RedisService to method getData with key={}", key);
        return redisDao.getData(key);
    }

    public Boolean deleteData(String key) {
        log.info("Entered RedisService to method deleteData with key={}", key);
        return redisDao.deleteData(key);
    }
}
