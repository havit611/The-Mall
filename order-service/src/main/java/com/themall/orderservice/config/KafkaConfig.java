package com.themall.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // 添加可靠性配置
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // ========== Order Service 发布的事件 ==========

    // 订单领域事件 - 所有订单相关的事件
    @Bean
    public NewTopic orderDomainEvents() {
        return TopicBuilder.name("order-domain-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // 支付请求事件 - 发送给支付服务的请求
    @Bean
    public NewTopic paymentRequestEvents() {
        return TopicBuilder.name("payment-request-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // 库存请求事件 - 发送给库存服务的请求
    @Bean
    public NewTopic inventoryRequestEvents() {
        return TopicBuilder.name("inventory-request-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}