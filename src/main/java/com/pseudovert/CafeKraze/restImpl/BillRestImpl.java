package com.pseudovert.CafeKraze.restImpl;

import com.pseudovert.CafeKraze.POJO.Bill;
import com.pseudovert.CafeKraze.rest.BillRest;
import com.pseudovert.CafeKraze.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class BillRestImpl implements BillRest {
    @Autowired
    BillService billService;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        return billService.generateReport(requestMap);
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        return billService.getBills();
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        return billService.getPdf(requestMap);
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        return billService.deleteBill(id);
    }
}
