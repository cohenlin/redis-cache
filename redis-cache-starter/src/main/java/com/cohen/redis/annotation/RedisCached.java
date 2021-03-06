package com.cohen.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在方法上，表示此方法执行前去redis缓存中取，查到结果存入redis缓存
 *
 * @author 林金成
 * @date 2018/5/1213:00
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisCached {
}
