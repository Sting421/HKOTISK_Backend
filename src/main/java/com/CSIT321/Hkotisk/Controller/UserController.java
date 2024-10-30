package com.CSIT321.Hkotisk.Controller;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import com.CSIT321.Hkotisk.Constant.ResponseCode;
import com.CSIT321.Hkotisk.Constant.WebConstants;
import com.CSIT321.Hkotisk.DTO.AddToCartDTO;
import com.CSIT321.Hkotisk.Entity.CartEntity;
import com.CSIT321.Hkotisk.Entity.OrderEntity;
import com.CSIT321.Hkotisk.Entity.ProductEntity;
import com.CSIT321.Hkotisk.Entity.User;
import com.CSIT321.Hkotisk.Exception.CartCustomException;
import com.CSIT321.Hkotisk.Exception.OrderCustomException;
import com.CSIT321.Hkotisk.Exception.ProductCustomException;
import com.CSIT321.Hkotisk.Exception.UserCustomException;
import com.CSIT321.Hkotisk.Repository.CartRepository;
import com.CSIT321.Hkotisk.Repository.OrderRepository;
import com.CSIT321.Hkotisk.Repository.ProductRepository;
import com.CSIT321.Hkotisk.Repository.UserRepository;
import com.CSIT321.Hkotisk.Response.CartResponse;
import com.CSIT321.Hkotisk.Response.ProductResponse;
import com.CSIT321.Hkotisk.Response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/user")
public class UserController {

    private static Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepository prodRepo;

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private OrderRepository ordRepo;




    // Lists All Products
    @GetMapping("/product")
    public ResponseEntity<ProductResponse> getProducts(Authentication auth) throws IOException {
        ProductResponse resp = new ProductResponse();
        try {
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.LIST_SUCCESS_MESSAGE);
            resp.setOblist(prodRepo.findAll());
        } catch (Exception e) {
            throw new ProductCustomException("Unable to retrieve products, please try again");
        }
        return new ResponseEntity<ProductResponse>(resp, HttpStatus.OK);
    }

    // Adds a product to the cart
    @PostMapping("/addToCart")
    public ResponseEntity<ServerResponse> addToCart(@RequestBody AddToCartDTO cart, Authentication auth) throws IOException {
        ServerResponse resp = new ServerResponse();
        if (!cart.getSize().isEmpty()){
            cart.setSize(cart.getSize().toUpperCase());
        }
        try {
            User loggedUser = userRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new UserCustomException(auth.getName()));
            ProductEntity cartItem = prodRepo.findByProductId(cart.getProductId());

            // Check if the selected size is valid
            if (cart.getSize() != null && !Arrays.asList(cartItem.getSizes()).contains(cart.getSize())) {
                throw new CartCustomException("Invalid size selected");
            }

            // Check if the item already exists in the cart
            Optional<CartEntity> existingCartItem = cartRepo.findByEmailAndProductIdAndProductSize(
                    loggedUser.getEmail(), cart.getProductId(), cart.getSize());



            if (existingCartItem.isPresent()) {
                // Update the quantity of the existing item
                CartEntity confirmedExistingCartItem = existingCartItem.get();
                confirmedExistingCartItem.setQuantity(confirmedExistingCartItem.getQuantity() + cart.getQuantity());
                cartRepo.save(confirmedExistingCartItem);
            } else {
                // Create a new cart item
                CartEntity buf = new CartEntity();
                buf.setEmail(loggedUser.getEmail());
                buf.setQuantity(cart.getQuantity());
                buf.setPrice(cart.getPrice() != 0.0 ? cart.getPrice() : cartItem.getPrice());
                buf.setProductId(cart.getProductId());
                buf.setProductName(cartItem.getProductName());
                buf.setProductSize(cart.getSize()); // Store the selected size
                buf.setDateAdded(new Date());

                cartRepo.save(buf);
            }

            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.CART_UPD_MESSAGE_CODE);
        } catch (Exception e) {
            throw new CartCustomException("Unable to add product to cart, please try again");
        }
        return new ResponseEntity<ServerResponse>(resp, HttpStatus.OK);
    }

    // Query to get all carts belonging to an email
    @GetMapping("/cart")
    public ResponseEntity<CartResponse> viewCart(Authentication auth) throws IOException {
        logger.info("Inside View cart request method");
        System.out.println(auth.getName());
        CartResponse resp = new CartResponse();
        try {
            User loggedUser = userRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new UserCustomException("User not found: " + auth.getName()));
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.VW_CART_MESSAGE);
            resp.setOblist(cartRepo.findByEmail(loggedUser.getEmail()));
        } catch (Exception e) {
            logger.severe("Error retrieving cart items: " + e.getMessage());
            throw new CartCustomException("Unable to retrieve cart items, please try again");
        }

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @PutMapping("/cart")
    public ResponseEntity<CartResponse> updateCart(@RequestBody HashMap<String, String> cart, Authentication auth)
            throws IOException {

        CartResponse resp = new CartResponse();
        try {
            User loggedUser = userRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new UserCustomException(auth.getName()));
            CartEntity studentCart = cartRepo.findByCartIdAndEmail(Integer.parseInt(cart.get("id")), loggedUser.getEmail());
            studentCart.setQuantity(Integer.parseInt(cart.get("quantity")));
            cartRepo.save(studentCart);
            List<CartEntity> studentCarts = cartRepo.findByEmail(loggedUser.getEmail());
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.UPD_CART_MESSAGE);
            resp.setOblist(studentCarts);
        } catch (Exception e) {
            throw new CartCustomException("Unable to update cart items, please try again");
        }

        return new ResponseEntity<CartResponse>(resp, HttpStatus.OK);
    }

    @DeleteMapping("/cart")
    public ResponseEntity<CartResponse> delCart(@RequestParam(name = WebConstants.BUF_ID) String cartId,
                                                Authentication auth) throws IOException {

        CartResponse resp = new CartResponse();
        try {
            User loggedUser = userRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new UserCustomException(auth.getName()));
            cartRepo.deleteByCartIdAndEmail(Integer.parseInt(cartId), loggedUser.getEmail());
            List<CartEntity> studentCarts = cartRepo.findByEmail(loggedUser.getEmail());
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.DEL_CART_SUCCESS_MESSAGE);
            resp.setOblist(studentCarts);
        } catch (Exception e) {
            throw new CartCustomException("Unable to delete cart items, please try again");
        }
        return new ResponseEntity<CartResponse>(resp, HttpStatus.OK);
    }

    @GetMapping("/order")
    public ResponseEntity<ServerResponse> placeOrder(Authentication auth) throws IOException {

        ServerResponse resp = new ServerResponse();
        try {
            User loggedUser = userRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new UserCustomException(auth.getName()));
            OrderEntity po = new OrderEntity();
            po.setEmail(loggedUser.getEmail());
            Date date = new Date();
            po.setOrderDate(date);
            po.setOrderStatus(ResponseCode.ORD_STATUS_CODE);
            double total = 0;
            List<CartEntity> studentCarts = cartRepo.findAllByEmail(loggedUser.getEmail());
            for (CartEntity studentCart : studentCarts) {
                total = +(studentCart.getQuantity() * studentCart.getPrice());
            }
            po.setTotalCost(total);
            OrderEntity res = ordRepo.save(po);
            studentCarts.forEach(studentCart -> {
                studentCart.setOrderId(res.getOrderId());
                cartRepo.save(studentCart);
            });
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.ORD_SUCCESS_MESSAGE);
        } catch (Exception e) {
            throw new OrderCustomException("Unable to place order, please try again later");
        }
        return new ResponseEntity<ServerResponse>(resp, HttpStatus.OK);
    }
}
