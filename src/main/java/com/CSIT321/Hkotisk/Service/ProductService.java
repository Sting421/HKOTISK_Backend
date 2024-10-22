package com.CSIT321.Hkotisk.Service;

import com.CSIT321.Hkotisk.Repository.ProductRepository;
import com.CSIT321.Hkotisk.Entity.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public ProductEntity insertProduct(ProductEntity entity){
        if (getProductByName(entity.getName()) != null){
            throw new IllegalArgumentException("Product already exists.");
        }
        return productRepository.save(entity);
    }

    public List<ProductEntity> getProducts() {return productRepository.findAll();}

    public ProductEntity getProductById(int id) {return productRepository.findById(id).get();}

    public ProductEntity getProductByName(String name) {
        return productRepository.findByName(name);
    }

    public String getProductFilenameById(int id) {
        ProductEntity product = productRepository.findById(id).orElse(null);
        if (product != null) {
            return product.getFilename();
        } else {
            return null;
        }
    }

    public ProductEntity updateProduct(int id, ProductEntity newProductDetails) throws NoSuchFieldException {
        ProductEntity product = new ProductEntity();
        if (productRepository.findById(id).isPresent()){
            product = productRepository.findById(id).get();

            product.setName(newProductDetails.getName());
            product.setFilename(newProductDetails.getFilename());
            product.setDescription(newProductDetails.getDescription());
            product.setPrice(newProductDetails.getPrice());
            product.setCategory(newProductDetails.getCategory());
            product.setQuantity(newProductDetails.getQuantity());

            productRepository.save(product);

        }else{
            throw new NoSuchFieldException("Product with ID: " + id +", cannot be found");
        }


        return productRepository.save(product);
    }

    public String deleteProduct(int id) {
        String msg = "";

        if (productRepository.findById(id).isPresent()){
            productRepository.deleteById(id);
            msg = "Student " + id +" is successfully deleted!";
        } else
            msg = "Student " + id + " does not exist.";
        return msg;
    }
}
