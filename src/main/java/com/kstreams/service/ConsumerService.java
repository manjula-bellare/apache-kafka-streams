package com.kstreams.service;

import com.kstreams.model.CustomerOrder;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class ConsumerService {
    @Value("${output.topic")
    private String outputTopic;
    @Value ("${bootstrap.servers.config}")
    private String bootstrapServersConfig;

    public List<CustomerOrder> consumeCustomerOrderEvents() {
        Properties properties = new Properties() {{
            putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, "customer-order-group");
            putIfAbsent(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
            putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomerOrder.class);

        }};
        List<CustomerOrder> customerOrderList = new ArrayList<>();

        try (Consumer<String, CustomerOrder> consumer = new KafkaConsumer<>(properties)) {
            consumer.subscribe(Collections.singletonList(outputTopic));
            while (true) {
                ConsumerRecords<String, CustomerOrder> consumerRecords = consumer.poll(Duration.ofSeconds(2));
                consumerRecords.forEach(consumerRecord -> {
                    CustomerOrder customerOrder = consumerRecord.value();
                    System.out.print("CustomerOrder details { ");
                    System.out.printf("OrderId: %s, ", customerOrder.getOrderId());
                    System.out.printf("Transaction event: %s }", customerOrder.getTransactionEvent());
                    customerOrderList.add(customerOrder);

                });
                return customerOrderList;
            }
        }
    }
}
