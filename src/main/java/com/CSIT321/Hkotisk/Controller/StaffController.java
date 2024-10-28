package com.CSIT321.Hkotisk.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.CSIT321.Hkotisk.Constant.ResponseCode;
import com.CSIT321.Hkotisk.Entity.OrderEntity;
import com.CSIT321.Hkotisk.Entity.ProductEntity;
import com.CSIT321.Hkotisk.Exception.OrderCustomException;
import com.CSIT321.Hkotisk.Exception.ProductCustomException;
import com.CSIT321.Hkotisk.Repository.CartRepository;
import com.CSIT321.Hkotisk.Repository.OrderRepository;
import com.CSIT321.Hkotisk.Repository.ProductRepository;
import com.CSIT321.Hkotisk.Response.Order;
import com.CSIT321.Hkotisk.Response.ProductResponse;
import com.CSIT321.Hkotisk.Response.ServerResponse;
import com.CSIT321.Hkotisk.Response.ViewOrderResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private ProductRepository prodRepo;

    @Autowired
    private OrderRepository ordRepo;

    @Autowired
    private CartRepository cartRepo;


    @PostMapping("/product")
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductEntity input) throws IOException {
        ProductResponse resp = new ProductResponse();
        try {
            ProductEntity prod = new ProductEntity();
            prod.setDescription(input.getDescription());
            prod.setPrice(input.getPrice());
            prod.setProductName(input.getProductName());
            prod.setQuantity(input.getQuantity());
            if (input.getProductImage() != null) {
                prod.setProductImage(input.getProductImage());
            }
            prodRepo.save(prod);
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.ADD_SUCCESS_MESSAGE);
            resp.setOblist(prodRepo.findAll());
        } catch (Exception e) {
            throw new ProductCustomException("Unable to save product details, please try again");
        }
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

   @PutMapping("/product")
    public ResponseEntity<ServerResponse> updateProducts(@Valid @RequestBody ProductEntity productDTO) throws IOException {
        ServerResponse resp = new ServerResponse();
        try {
            ProductEntity prod;
            if (productDTO.getProductImage() != null) {
                prod = new ProductEntity(productDTO.getProductId(), productDTO.getDescription(), productDTO.getProductName(),
                        productDTO.getPrice(), productDTO.getQuantity(), productDTO.getProductImage());
            } else {
                ProductEntity prodOrg = prodRepo.findByProductId(productDTO.getProductId());
                prod = new ProductEntity(productDTO.getProductId(), productDTO.getDescription(), productDTO.getProductName(),
                        productDTO.getPrice(), productDTO.getQuantity(), prodOrg.getProductImage());
            }
            prodRepo.save(prod);
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.UPD_SUCCESS_MESSAGE);
        } catch (Exception e) {
            throw new ProductCustomException("Unable to update product details, please try again");
        }
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
    @DeleteMapping("/product")
    public ResponseEntity<ProductResponse> delProduct(@Valid @RequestBody ProductEntity productDTO) throws IOException {
        ProductResponse resp = new ProductResponse();
        try {
            prodRepo.deleteByProductId(productDTO.getProductId());
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.DEL_SUCCESS_MESSAGE);
        } catch (Exception e) {
            throw new ProductCustomException("Unable to delete product details, please try again");
        }
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping("/orders")
    public ResponseEntity<ViewOrderResponse> viewOrders() throws IOException {

        ViewOrderResponse resp = new ViewOrderResponse();
        try {
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.VIEW_SUCCESS_MESSAGE);
            List<Order> orderList = new ArrayList<>();
            List<OrderEntity> poList = ordRepo.findAll();
            poList.forEach((po) -> {
                Order ord = new Order();
                ord.setOrderBy(po.getEmail());
                ord.setOrderId(po.getOrderId());
                ord.setOrderStatus(po.getOrderStatus());
                ord.setProducts(cartRepo.findAllByOrderId(po.getOrderId()));
                orderList.add(ord);
            });
            resp.setOrderlist(orderList);
        } catch (Exception e) {
            throw new OrderCustomException("Unable to retrieve orders, please try again");
        }

        return new ResponseEntity<ViewOrderResponse>(resp, HttpStatus.OK);
    }

    @PostMapping("/order")
    public ResponseEntity<ServerResponse> updateOrders(@Valid @RequestBody OrderEntity orderDTO) throws IOException {
        ServerResponse resp = new ServerResponse();
        try {
            OrderEntity pc = ordRepo.findByOrderId(orderDTO.getOrderId());
            pc.setOrderStatus(orderDTO.getOrderStatus());
            pc.setOrderDate(new Date(System.currentTimeMillis()));
            ordRepo.save(pc);
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.UPD_ORD_SUCCESS_MESSAGE);
        } catch (Exception e) {
            throw new OrderCustomException("Unable to retrieve orders, please try again");
        }
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}