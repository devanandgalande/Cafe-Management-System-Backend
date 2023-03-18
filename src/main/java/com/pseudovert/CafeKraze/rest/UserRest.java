package com.pseudovert.CafeKraze.rest;

import com.pseudovert.CafeKraze.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/user")
public interface UserRest {
    @PostMapping(path = "/signup")
    ResponseEntity<String> signUp(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/login")
    ResponseEntity<String> login(@RequestBody()Map<String, String> requestMap);

    @GetMapping(path = "/get")
    ResponseEntity<List<UserWrapper>> getAllUsers();

    @PostMapping(path = "/update")
    ResponseEntity<String> updateStatus(@RequestBody() Map<String, String> requestMap);

    @GetMapping(path = "/checkToken")
    ResponseEntity<String> checkToken();

    @PostMapping(path = "/changePassword")
    ResponseEntity<String> changePassword(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/forgotPassword")
    ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> requestMap);

}
