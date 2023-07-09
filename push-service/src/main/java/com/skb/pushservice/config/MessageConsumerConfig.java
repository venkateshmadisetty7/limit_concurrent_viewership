package com.skb.pushservice.config;

import com.skb.pushservice.domain.Exist.ExistDto;
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
public class MessageConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootStrapServers;

    private static final String existGroup = "exist-info-group";
    private static final String successResponse = "success-response-group";

    @Bean
    Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, existGroup);
        return props;
    }

    @Bean
    Map<String, Object> successConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, successResponse);
        return props;
    }

    @Bean
    public ConsumerFactory<String, ExistDto.Response> existConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(ExistDto.Response.class, false));
    }

    @Bean
    public ConsumerFactory<String, String> successConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(successConsumerConfigs(), new StringDeserializer(), new StringDeserializer());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ExistDto.Response>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ExistDto.Response> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(existConsumerFactory());
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> successKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(successConsumerFactory());
        return factory;
    }

}

