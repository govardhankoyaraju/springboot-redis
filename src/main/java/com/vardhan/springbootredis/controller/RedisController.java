package com.vardhan.springbootredis.controller;

import com.vardhan.springbootredis.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v1/redis")
@RequiredArgsConstructor

public class RedisController {

    private final RedisService redisService;

    @PostMapping
    public ResponseEntity<HttpStatus> saveData(@NonNull @RequestParam String key,
            @RequestBody Map<String, Object> cacheDataMap) {
        log.info("Entered RedisController to method saveData with key={} & data={}", key, cacheDataMap);
        redisService.saveData(key, cacheDataMap);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getData(@NonNull @RequestParam String key) {
        log.info("Entered RedisController to method getData with key={}", key);
        Map<String, Object> cacheData = redisService.getData(key);
        if (Objects.isNull(cacheData))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok().body(cacheData);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteData(@NonNull @RequestParam String key) {
        log.info("Entered RedisController to method deleteData with key={}", key);
        boolean isDeleted = redisService.deleteData(key);
        if (!isDeleted)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
