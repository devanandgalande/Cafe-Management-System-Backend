package com.pseudovert.CafeKraze.serviceImpl;

import com.pseudovert.CafeKraze.JWT.JwtFilter;
import com.pseudovert.CafeKraze.POJO.Category;
import com.pseudovert.CafeKraze.POJO.Product;
import com.pseudovert.CafeKraze.constants.CafeConstants;
import com.pseudovert.CafeKraze.dao.ProductDao;
import com.pseudovert.CafeKraze.service.ProductService;
import com.pseudovert.CafeKraze.utils.CafeUtils;
import com.pseudovert.CafeKraze.wrapper.ProductWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductDao productDao;
    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        log.info("Request to add new product : {}", requestMap);

        try {
            if (jwtFilter.isAdmin()) {
                if (validateProductMap(requestMap, false)) {
                    productDao.save(getProductFromMap(requestMap, false));
                    return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_ADD_SUCCESS, HttpStatus.OK);
                } else
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")) {
            if (requestMap.containsKey("id") && validateId)
                return true;
            else return !validateId;
        }
        return false;
    }

    private Product getProductFromMap(Map<String, String> requestMap, boolean isUpdate) {
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));
        Product product = new Product();
        if (isUpdate)
            product.setId(Integer.parseInt(requestMap.get("id")));
        else
            product.setStatus("true");
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        product.setDescription(requestMap.get("description"));
        return product;
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProducts() {
        log.info("Request to fetch all products..");
        try {
            return new ResponseEntity<>(productDao.getAllProducts(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        log.info("Request to update product : {}", requestMap);

        try {
            if (jwtFilter.isAdmin()) {
                if (validateProductMap(requestMap, true)) {
                    Optional<Product> optionalProduct = productDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (optionalProduct.isPresent()) {
                        Product product = getProductFromMap(requestMap, true);
                        product.setStatus(optionalProduct.get().getStatus());
                        productDao.save(product);
                        return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_UPDATE_SUCCESS, HttpStatus.OK);
                    } else
                        return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_ID_NOT_EXIST, HttpStatus.OK);

                } else
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer productId) {
        log.info("Service Request to delete product : {}", productId);

        if (jwtFilter.isAdmin()) {
            Optional<Product> optionalProduct = productDao.findById(productId);
            if (optionalProduct.isPresent()) {
                productDao.deleteById(productId);
                return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_DELETE_SUCCESS, HttpStatus.OK);
            } else
                return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_ID_NOT_EXIST, HttpStatus.OK);
        } else
            return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        log.info("Service Request to update status for product : {}", requestMap);

        if (jwtFilter.isAdmin()) {
            Optional<Product> optionalProduct = productDao.findById(Integer.valueOf(requestMap.get("id")));
            if (optionalProduct.isPresent()) {
                optionalProduct.get().setStatus(requestMap.get("status"));
                productDao.save(optionalProduct.get());
                return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_STATUS_UPDATE_SUCCESS, HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_ID_NOT_EXIST, HttpStatus.OK);
            }
        } else
            return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer categoryId) {
        log.info("Service Request to getByCategory product : {}", categoryId);

        return new ResponseEntity<>(productDao.getProductByCategory(categoryId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ProductWrapper> getById(Integer productId) {
        log.info("Service Request to getById product : {}", productId);

        return new ResponseEntity<>(productDao.getProductById(productId), HttpStatus.OK);
    }
}
