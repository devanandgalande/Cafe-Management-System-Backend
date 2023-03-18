package com.pseudovert.CafeKraze.rest;

import com.pseudovert.CafeKraze.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/product")
public interface ProductRest {

    @PostMapping(path = "/add")
    ResponseEntity<String> addNewProduct(@RequestBody(required = true)
                                         Map<String, String> requestMap);

    @GetMapping(path = "/get")
    ResponseEntity<List<ProductWrapper>> getAllProducts();

    @PostMapping(path = "/update")
    ResponseEntity<String> updateProduct(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable(name = "id") Integer productId);

    @PostMapping(path = "/updateStatus")
    ResponseEntity<String> updateStatus(@RequestBody(required = true) Map<String, String> requestMap);

    @GetMapping(path = "/getByCategory/{id}")
    ResponseEntity<List<ProductWrapper>> getByCategory(@PathVariable(name = "id") Integer categoryId);

    @GetMapping(path = "/getById/{id}")
    ResponseEntity<ProductWrapper> getById(@PathVariable(name = "id") Integer productId);


}
