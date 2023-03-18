package com.pseudovert.CafeKraze.restImpl;

import com.pseudovert.CafeKraze.rest.DashboardRest;
import com.pseudovert.CafeKraze.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DashboardRestImpl implements DashboardRest {

    @Autowired
    DashboardService dashboardService;

    @Override
    public ResponseEntity<Map<String, Long>> getDetails() {
        return dashboardService.getDetails();
    }
}
