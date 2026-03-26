package com.xjzai1.xjzai1picturebackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local") // 通过注解可以指定运行环境
public class RedisStringTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedisStringOperations() {
        // 获取操作对象
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();

        // Key 和 Value
        String key = "testKey";
        String value = "testValue";

        // 1. 测试新增或更新操作
        valueOps.set(key, value);
        String storedValue = valueOps.get(key);
        assertEquals(value, storedValue, "Stored value does not match expected");

        // 2. 测试修改操作
        String updatedValue = "updatedValue";
        valueOps.set(key, updatedValue);
        storedValue = valueOps.get(key);
        assertEquals(updatedValue, storedValue, "Updated value does not match expected");

        // 3. 测试查询操作
        storedValue = valueOps.get(key);
        assertNotNull(storedValue, "Query returned a null value");
        assertEquals(updatedValue, storedValue, "Query result does not match expected");

        // 4. 测试删除操作
        stringRedisTemplate.delete(key);
        storedValue = valueOps.get(key);
        assertNull(storedValue, "Value is not null after deletion");
    }
}
