package com.CSIT321.Hkotisk.Service;

import com.CSIT321.Hkotisk.Repository.OrderRepository;
import com.CSIT321.Hkotisk.Repository.StudentRepository;
import com.CSIT321.Hkotisk.Entity.OrderEntity;
import com.CSIT321.Hkotisk.Entity.StudentEntity;
import com.CSIT321.Hkotisk.Entity.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public OrderEntity insertOrder(OrderEntity order) {
        try {
            // Fetch the student from the repository by its ID
            StudentEntity student = studentRepository.findById(order.getStudent().getSid())
                    .orElseThrow(() -> new NoSuchElementException("Student with ID " + order.getStudent().getSid() + " not found."));

            // Validate the contact info
            if (!isValidContactInfo(order.getContactInfo())) {
                throw new IllegalArgumentException("Invalid contact info. It should be 11 digits starting with 09.");
            }

            // Update the student's user type and save it
            studentRepository.save(student);

            // Set the student for the order and save the order
            order.setStudent(student);
            OrderEntity savedOrder = orderRepository.save(order);

            // Assuming you have a setOrder method in StudentEntity to set the order for the student
            student.setOrder(savedOrder);
            studentRepository.save(student);

            return savedOrder;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to insert order.", e);
        }
    }

    public List<OrderEntity> getAllOrder() {
        return orderRepository.findAll();
    }

    public OrderEntity updateOrder(int orderId, OrderEntity newOrderDetails) {
        OrderEntity order = new OrderEntity();
        try {
            //1. search the ID  number of the student that will be updated
            order = orderRepository.findById(orderId).get();

            //2. update the record
            order.setContactInfo(newOrderDetails.getContactInfo());
            order.setLocation(newOrderDetails.getLocation());
            order.setPaymentMethod(newOrderDetails.getPaymentMethod());
            order.setPrice(newOrderDetails.getPrice());
            order.setTotalAmount(newOrderDetails.getTotalAmount());
            order.setFoodName(newOrderDetails.getFoodName());
            order.setQuantity(newOrderDetails.getQuantity());
            order.setLocation(newOrderDetails.getLocation());

        } catch (NoSuchElementException ex) {
            throw new NoSuchElementException("Student" + orderId + "does not exist!");
        }
        return orderRepository.save(order);
    }


    public String deleteOrder(int orderId) {
        Optional<OrderEntity> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            OrderEntity order = optionalOrder.get();
            StudentEntity student = order.getStudent();

            if (student != null) {
                // Remove the reference to the order and update the user type
                student.setOrder(null);
                student.setUserType(UserType.STUDENT);
                studentRepository.save(student);
            }

            // Delete the order
            orderRepository.deleteById(orderId);
            return "Order " + orderId + " is successfully deleted!";
        } else {
            return "Order " + orderId + " does not exist.";
        }
    }

    private boolean isValidContactInfo(String contactInfo) {
        // Check if contactInfo is 11 digits starting with 09
        return contactInfo != null && Pattern.matches("^09\\d{9}$", contactInfo);
    }
}
