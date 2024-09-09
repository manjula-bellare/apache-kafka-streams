package com.kstreams.model;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Map;

@Service
public class CustomerOrder implements Serializable, Serializer<CustomerOrder>, Deserializer<CustomerOrder> {
    @Serial
    private static final long serialVersionUID = 1L;
    private String orderId;
    private String productId;
    private String emailId;
    private String customerName;
    private String address;
    private Long timestamp;
    private String transactionEvent;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransactionEvent() {
        return transactionEvent;
    }

    public void setTransactionEvent(String transactionEvent) {
        this.transactionEvent = transactionEvent;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public CustomerOrder deserialize(String topic, byte[] data) {
        CustomerOrder customerOrder = null;
        try
        {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            customerOrder = (CustomerOrder) objectInputStream.readObject();
            objectInputStream.close();
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return customerOrder;
    }

    @Override
    public CustomerOrder deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public CustomerOrder deserialize(String topic, Headers headers, ByteBuffer data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public byte[] serialize(String topic, CustomerOrder customerOrder) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try
        {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(customerOrder);
            byteArrayOutputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public byte[] serialize(String topic, Headers headers, CustomerOrder data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}
