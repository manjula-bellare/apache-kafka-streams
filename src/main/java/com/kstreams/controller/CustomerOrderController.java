package com.kstreams.controller;

import com.kstreams.model.CustomerOrder;
import com.kstreams.service.ConsumerService;
import com.kstreams.service.CustomerOrderStreams;
import com.kstreams.service.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class CustomerOrderController {
    @Autowired
    CustomerOrder customerOrder;
    @Autowired
    ProducerService producerService;
    @Autowired
    CustomerOrderStreams customerOrderStreams;
    @Autowired
    ConsumerService consumerService;
    @PostMapping(value = "/api/customers/customerOrder", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void subscribeToCustomerOrder(@RequestBody CustomerOrder customerOrder) {
        producerService.getCustomerOrderEvents(Arrays.asList(customerOrder));
    }
    @GetMapping(value = "/api/customers/customerOrderStream")
    public void streamCustomerOrder() {
        customerOrderStreams.streamCustomerOrderEvents();
    }

    @GetMapping(value = "/api/customers/customerOrderEvents", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<List<CustomerOrder>> getCustomerOrderEvents() {
        return new ResponseEntity<List<CustomerOrder>>(consumerService.consumeCustomerOrderEvents(), HttpStatus.OK);
    }

}
