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

// 配置生产者工厂、消息模板和主题定义
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;


    // 配置Kafka生产者工厂：创建Kafka生产者实例的工厂，定义生产者的核心配置
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        // 1. 配置Kafka集群地址：生产者连接到哪个Kafka集群
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // 2. 配置Key Serializer：将消息Key从Java String转换为字节数组
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 3. 配置Value Serializer：将消息内容从Java String转换为字节数组
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // 配置KafkaTemplate
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        // 使用上面配置的生产者工厂创建模板
        return new KafkaTemplate<>(producerFactory());
    }


    // 定义并自动创建Kafka orderEvents topic：（如果不存在）应用启动时自动在Kafka中创建所需的主题
    // 3个分区 ～ 最多3个消费者并行消费
    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name("order-events")
            .partitions(3)
            .replicas(1)
            .build();
    }

    // TODO：could have paymentEventsTopic, userActivityTopic, inventoryUpdatesTopic, notificationsTopic later
    // @Bean
    //    public NewTopic paymentEventsTopic() {
    //
    //    }
}