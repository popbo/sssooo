package com.stdc.topology2d.util;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdGenerator {
    // 使用MyBatis-Plus内置的DefaultIdentifierGenerator
    private final IdentifierGenerator idGenerator = new DefaultIdentifierGenerator();

    public Long nextId() {
        return idGenerator.nextId(null).longValue();
    }

    public Long nextId(Object entity) {
        return idGenerator.nextId(entity).longValue();
    }
}
