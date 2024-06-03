package com.ecommerce.springboot.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.springboot.exception.LoginException;
import com.ecommerce.springboot.exception.OrderException;
import com.ecommerce.springboot.models.Customer;
import com.ecommerce.springboot.models.Order;
import com.ecommerce.springboot.models.OrderDto;
import com.ecommerce.springboot.repository.OrderRepository;
import com.ecommerce.springboot.service.OrderService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;


    @PostMapping("/order/place")
    public ResponseEntity<Order> addNewOrder(@RequestBody OrderDto orderDto, @RequestHeader String token) throws LoginException, OrderException{
        Order newOrder = orderService.saveOrder(orderDto, token);
        return new ResponseEntity<Order>(newOrder, HttpStatus.CREATED); 
    }

    @GetMapping("/orders")
    public List<Order> getAllOrders() throws OrderException{
        List<Order> allOrders = orderService.getAllOrders();
        return allOrders;
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Order> ordersByOrderid(@PathVariable("orderId") Integer orderId) throws OrderException{
        return new ResponseEntity<>(orderService.getOrderByOrderId(orderId),HttpStatus.OK);
    }

    @DeleteMapping("/order/{orderId}")
    public Order cancelOrderByOrderId(@PathVariable ("orderId")Integer orderId, @RequestHeader String token ) throws OrderException{
        return orderService.cancelOrderByOrderId(orderId, token);
    }

    @PutMapping("/order/{orderId}")
    public ResponseEntity<Order> updateOrderByOrderId(@RequestBody OrderDto orderDto, @PathVariable ("orderId") Integer orderId, @RequestHeader String token) throws OrderException, LoginException{
        Order existingOrder = orderService.updateOrderByOrderId(orderDto, orderId, token);
        return new ResponseEntity<Order>(existingOrder,HttpStatus.OK);
    }

    @GetMapping("/order/by/date")
    public List<Order> getOrdersByDate(@RequestParam LocalDate date ) throws OrderException{
        // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-mm-yyyy");
        // LocalDate ld = LocalDate.parse(date,dtf);
        return orderService.getAllOrdersByDate(date);
    }

    @GetMapping("customer/{orderId}")
    public Customer customerDetailsByOrderId(@PathVariable("orderId") Integer orderId) throws OrderException{
        return orderService.getCustomerByOrderId(orderId);
    }
}
