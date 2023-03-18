package com.pseudovert.CafeKraze.dao;

import com.pseudovert.CafeKraze.POJO.Product;
import com.pseudovert.CafeKraze.wrapper.ProductWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {
    @Query(value = "select new com.pseudovert.CafeKraze.wrapper.ProductWrapper(p.id, p.name, p.description," +
            "p.price, p.status, p.category.id, p.category.name) from Product p")
    List<ProductWrapper> getAllProducts();

    @Query(value = "select new com.pseudovert.CafeKraze.wrapper.ProductWrapper(p.id, p.name) from Product p" +
            " where p.category.id=:id and p.status='true'")
    List<ProductWrapper> getProductByCategory(@Param("id") Integer categoryId);

    @Query(value = "select new com.pseudovert.CafeKraze.wrapper.ProductWrapper(p.id, p.name, p.description," +
            "p.price) from Product p where p.id = :id")
    ProductWrapper getProductById(@Param("id") Integer productId);
}
