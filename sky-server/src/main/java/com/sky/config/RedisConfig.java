package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		log.info("开始配置 RedisTemplate ...");
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		// 使用 StringRedisSerializer 来序列化和反序列化 redis 的 key 值
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		// Hash 的 key 也采用 String 的序列化方式
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		// 使用 Jackson2JsonRedisSerializer 来序列化和反序列化 redis 的 value 值
		// 其他的序列化方式还可以使用 GenericJackson2JsonRedisSerializer
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		// Hash 的 value 也采用 Jackson2JsonRedisSerializer 的序列化方式
		redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		redisTemplate.afterPropertiesSet();
		log.info("RedisTemplate 配置成功!");
		return redisTemplate;
	}
}
