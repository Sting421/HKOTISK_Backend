package com.CSIT321.Hkotisk.Controller;

import com.CSIT321.Hkotisk.Entity.OrderEntity;
import com.CSIT321.Hkotisk.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public OrderEntity insertOrder(@RequestBody OrderEntity order) {
        return orderService.insertOrder(order);
    }

    @GetMapping
    public List<OrderEntity> getAllOrder() {
        return orderService.getAllOrder();
    }

    @PutMapping
    public OrderEntity updateOrder(@RequestParam int orderId, @RequestBody OrderEntity newOrderDetails) {
        return orderService.updateOrder(orderId, newOrderDetails);
    }

    @DeleteMapping
    public String deleteOrder(@RequestParam int orderId) {
        return orderService.deleteOrder(orderId);
    }
}
