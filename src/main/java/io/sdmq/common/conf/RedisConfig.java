package io.sdmq.common.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.SerializationUtils;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        final RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new RedisSerializer() {
            @Override
            public byte[] serialize(Object o) throws SerializationException {
                return SerializationUtils.serialize(o);
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                return SerializationUtils.deserialize(bytes);
            }
        });
        //template.setHashValueSerializer( new GenericToStringSerializer< Object >( Object.class ) );
        template.setValueSerializer(new RedisSerializer() {
            @Override
            public byte[] serialize(Object o) throws SerializationException {
                return SerializationUtils.serialize(o);
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                return SerializationUtils.deserialize(bytes);
            }
        });
        return template;
    }
}