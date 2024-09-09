package com.kstreams.service;

import com.kstreams.model.CustomerOrder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
public class CustomerOrderStreams {
    @Value("${application.id.config")
    private String applicationIdConfig;
    @Value ("${bootstrap.servers.config}")
    private String bootstrapServersConfig;
    @Value ("${input.topic}")
    private String inputTopic;
    @Value ("${output.topic}")
    private String outputTopic;
    public void streamCustomerOrderEvents(final StreamsBuilder builder) {
        try {
            final KStream<String, CustomerOrder> source = builder.stream(inputTopic, Consumed.with(Serdes.String(), GenericSerde.CustomerOrder()));
            source.selectKey((key, value) -> value.getOrderId()).to(outputTopic, Produced.with(Serdes.String(), GenericSerde.CustomerOrder()));
        } catch(Exception e) {
            System.out.println("Could not write to output topic. "+ e);
        }

    }

    public void streamCustomerOrderEvents() {
        StreamsBuilder builder = new StreamsBuilder();
        streamCustomerOrderEvents(builder);
        Properties properties = new Properties() {{
            putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, applicationIdConfig);
            putIfAbsent(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
            putIfAbsent(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);
            putIfAbsent(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
            putIfAbsent(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericSerde.CustomerOrderSerde.class);
            putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        }};

        try (KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), properties)) {
            final CountDownLatch latch = new CountDownLatch(1);

            Runtime.getRuntime().addShutdownHook(new Thread("streams-customerOrder-shutdown-hook") {
                @Override
                public void run() {
                    kafkaStreams.close(Duration.ofSeconds(1));
                    latch.countDown();
                }
            });

            try {
                kafkaStreams.start();
                latch.await();
            } catch (final Throwable e) {
                System.exit(1);
            }
            System.exit(0);
        }
    }

}

