package com.pseudovert.CafeKraze.serviceImpl;

import com.google.common.base.Strings;
import com.pseudovert.CafeKraze.JWT.JwtFilter;
import com.pseudovert.CafeKraze.POJO.Category;
import com.pseudovert.CafeKraze.constants.CafeConstants;
import com.pseudovert.CafeKraze.dao.CategoryDao;
import com.pseudovert.CafeKraze.service.CategoryService;
import com.pseudovert.CafeKraze.utils.CafeUtils;
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
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        log.info("Request to add new category : {}", requestMap);
        try {
            if (jwtFilter.isAdmin()) {
                if (validateCategoryMap(requestMap, false)) {
                    categoryDao.save(getCategoryFromMap(requestMap, false));
                    return CafeUtils.getResponseEntity(CafeConstants.CATEGORY_ADD_SUCCESS, HttpStatus.OK);
                }
            } else
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name"))
            if (requestMap.containsKey("id") && validateId) {
                return true;
            } else if (!validateId) {
                return true;
            }
        return false;
    }

    private Category getCategoryFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        if (isAdd) {
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        log.info("Request to get All category with filter : {}", filterValue);
        try {
            if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
                log.info("Returning All category with filter : {}", filterValue);
                return new ResponseEntity<>(categoryDao.getAllCategory(), HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        log.info("Request to update category : {}", requestMap);
        try {
            if (jwtFilter.isAdmin()) {
                if (validateCategoryMap(requestMap, true)) {
                    Optional<Category> optionalCategory = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (optionalCategory.isPresent()) {
                        categoryDao.save(getCategoryFromMap(requestMap, true));
                        return CafeUtils.getResponseEntity(CafeConstants.CATEGORY_UPDATE_SUCCESS, HttpStatus.OK);
                    } else
                        return CafeUtils.getResponseEntity(CafeConstants.CATEGORY_ID_NOT_EXIST, HttpStatus.OK);
                } else
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}