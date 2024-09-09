package com.kstreams.service;

import com.kstreams.model.CustomerOrder;
import org.apache.kafka.common.serialization.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GenericSerde {
    static public class WrapperSerde<T> implements Serde<T> {
        final private Serializer<T> serializer;
        final private Deserializer<T> deserializer;
        @Autowired
        private CustomerOrder customerOrder;


        public WrapperSerde(Serializer<T> serializer, Deserializer<T> deserializer) {
            this.serializer = serializer;
            this.deserializer = deserializer;
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            Serde.super.configure(configs, isKey);
            serializer.configure(configs, isKey);
            deserializer.configure(configs, isKey);
        }

        @Override
        public void close() {
            Serde.super.close();
            serializer.close();
            deserializer.close();
        }

        @Override
        public Serializer<T> serializer() {
            return this.serializer;
        }

        @Override
        public Deserializer<T> deserializer() {
            return this.deserializer;
        }
    } // wrapperSerde class

    static public final class CustomerOrderSerde extends Serdes.WrapperSerde<CustomerOrder> {
        public CustomerOrderSerde() {
            super(new CustomerOrder(), new CustomerOrder());
        }
    } // customerOrderSerde class

    static public Serde<CustomerOrder> CustomerOrder() {
        return new CustomerOrderSerde();
    }
} // genericSerde class