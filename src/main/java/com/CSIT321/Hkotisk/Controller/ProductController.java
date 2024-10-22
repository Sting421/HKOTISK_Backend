package com.CSIT321.Hkotisk.Controller;

import com.CSIT321.Hkotisk.Entity.ProductEntity;
import com.CSIT321.Hkotisk.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController  {
    @Autowired
    private ProductService productService;

    @PostMapping
    public ProductEntity insertProduct(@RequestBody ProductEntity product) {
        return productService.insertProduct(product);
    }

    @GetMapping
    public Iterable<ProductEntity> getAllProduct() {
        return productService.getProducts();
    }

    @GetMapping(params = "id")
    public ProductEntity getProduct(@RequestParam int id) {
        return productService.getProductById(id);
    }

    @GetMapping("/image")
    public String getAllProduct(@RequestParam int id) {
        return productService.getProductFilenameById(id);
    }

    @PutMapping
    public ProductEntity updateProduct(@RequestBody ProductEntity product, @RequestParam int id) throws NoSuchFieldException {
        return productService.updateProduct(id, product);
    }
    @DeleteMapping
    public String deleteOrderItem(@RequestParam int id) {
        return productService.deleteProduct(id);
    }
}
