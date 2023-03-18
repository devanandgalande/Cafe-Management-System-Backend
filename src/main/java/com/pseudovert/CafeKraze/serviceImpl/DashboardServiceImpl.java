package com.pseudovert.CafeKraze.serviceImpl;

import com.pseudovert.CafeKraze.dao.BillDao;
import com.pseudovert.CafeKraze.dao.CategoryDao;
import com.pseudovert.CafeKraze.dao.ProductDao;
import com.pseudovert.CafeKraze.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service @Slf4j
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    ProductDao productDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    BillDao billDao;

    @Override
    public ResponseEntity<Map<String, Long>> getDetails() {
        Map<String, Long> counts= new HashMap<>();
        counts.put("product", productDao.count());
        counts.put("category", categoryDao.count());
        counts.put("bill", billDao.count());
        return new ResponseEntity<>(counts, HttpStatus.OK);
    }
}
