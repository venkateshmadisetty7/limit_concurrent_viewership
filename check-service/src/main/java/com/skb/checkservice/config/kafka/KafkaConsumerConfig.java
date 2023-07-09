package com.skb.checkservice.config.kafka;

import com.skb.checkservice.domain.WatchInfo.WatchInfoDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootStrapServers;

    @Bean
    Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

    @Bean
    public ConsumerFactory<String, WatchInfoDto.Request> consumerFactory() {
        Map<String, Object> props = consumerConfigs();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "watch-info-group");
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(WatchInfoDto.Request.class, false));
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, WatchInfoDto.Request>> newKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, WatchInfoDto.Request> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, WatchInfoDto.Request> forceConsumerFactory() {
        Map<String, Object> props = consumerConfigs();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "force-info-group");
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(WatchInfoDto.Request.class, false));
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, WatchInfoDto.Request>> forceKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, WatchInfoDto.Request> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(forceConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, WatchInfoDto.Request> disconnectConsumerFactory() {
        Map<String, Object> props = consumerConfigs();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "disconnect-info-group");
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(WatchInfoDto.Request.class, false));
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, WatchInfoDto.Request>> disconnectKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, WatchInfoDto.Request> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(disconnectConsumerFactory());
        return factory;
    }
}
