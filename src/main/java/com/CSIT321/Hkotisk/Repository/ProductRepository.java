package com.CSIT321.Hkotisk.Repository;

import com.CSIT321.Hkotisk.Entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    ProductEntity findByName(String name);
}
