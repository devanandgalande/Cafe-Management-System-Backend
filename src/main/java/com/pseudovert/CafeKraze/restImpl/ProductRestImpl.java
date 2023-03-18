package com.pseudovert.CafeKraze.restImpl;

import com.pseudovert.CafeKraze.constants.CafeConstants;
import com.pseudovert.CafeKraze.rest.ProductRest;
import com.pseudovert.CafeKraze.service.ProductService;
import com.pseudovert.CafeKraze.utils.CafeUtils;
import com.pseudovert.CafeKraze.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ProductRestImpl implements ProductRest {

    @Autowired
    ProductService productService;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            return productService.addNewProduct(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProducts() {
        try {
            return productService.getAllProducts();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            return productService.updateProduct(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer productId) {
        return productService.deleteProduct(productId);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        return productService.updateStatus(requestMap);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer categoryId) {
        return productService.getByCategory(categoryId);
    }

    @Override
    public ResponseEntity<ProductWrapper> getById(Integer productId) {
        return productService.getById(productId);
    }


}
