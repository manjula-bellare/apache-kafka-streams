package com.kstreams.service;

import com.kstreams.model.CustomerOrder;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {
    @Value("${input.topic}")
    private String inputTopic;
    @Value("${output.topic")
    private String outputTopic;
    @Value ("${bootstrap.servers.config}")
    private  String bootstrapServersConfig;
    public void getCustomerOrderEvents(List<CustomerOrder> customerOrderList) {
        Properties  properties = new Properties() {{
            putIfAbsent(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
            putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CustomerOrder.class);
            putIfAbsent(ProducerConfig.ACKS_CONFIG, "1");
        }};
        try (final Producer<String, CustomerOrder> producer = new KafkaProducer<>(properties)) {
            customerOrderList.forEach(event -> producer.send(new ProducerRecord<>(inputTopic, event.getOrderId(), event), ((metadata, exception) -> {
                if (exception != null) {
                    System.err.printf("Producing CustomerOrder event %s resulted in error %s %n", event, exception);
                } else {
                    System.out.printf("Produced CustomerOrder record at offset %s with timestamp %d %n", metadata.offset(), metadata.timestamp());
                }
            })));

        }
    }
}
