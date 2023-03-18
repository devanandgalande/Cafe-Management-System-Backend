package com.pseudovert.CafeKraze.service;

import com.pseudovert.CafeKraze.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {
    ResponseEntity<String> addNewProduct(Map<String, String> requestMap);

    ResponseEntity<List<ProductWrapper>> getAllProducts();

    ResponseEntity<String> updateProduct(Map<String, String> requestMap);

    ResponseEntity<String> deleteProduct(Integer productId);

    ResponseEntity<String> updateStatus(Map<String, String> requestMap);

    ResponseEntity<List<ProductWrapper>> getByCategory(Integer categoryId);

    ResponseEntity<ProductWrapper> getById(Integer productId);
}
