package com.pseudovert.CafeKraze.serviceImpl;

import com.google.common.base.Strings;
import com.pseudovert.CafeKraze.JWT.CustomerUserDetailsService;
import com.pseudovert.CafeKraze.JWT.JwtFilter;
import com.pseudovert.CafeKraze.JWT.JwtUtil;
import com.pseudovert.CafeKraze.POJO.User;
import com.pseudovert.CafeKraze.constants.CafeConstants;
import com.pseudovert.CafeKraze.dao.UserDao;
import com.pseudovert.CafeKraze.service.UserService;
import com.pseudovert.CafeKraze.utils.CafeUtils;
import com.pseudovert.CafeKraze.utils.EmailUtils;
import com.pseudovert.CafeKraze.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    CustomerUserDetailsService customerUserDetailsService;
    @Autowired
    EmailUtils emailUtils;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Signup Request for {}", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                User userFound = userDao.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(userFound)) {
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Successfully Registered!", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Email Already Exist!", HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("password") &&
                requestMap.containsKey("contactNumber") && requestMap.containsKey("email");
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail((requestMap.get("email")));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("USER");
        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Login request for username {}", requestMap.get("email"));
        try{
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );

            if (auth.isAuthenticated()) {
                if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                                    customerUserDetailsService.getUserDetail().getRole())
                            + "\"}", HttpStatus.OK);
                } else
                    return CafeUtils.getResponseEntity("Wait for Admin Approval.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity("Bad Credentials!", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try{
            if (jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUsers(), HttpStatus.OK);
            } else
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try{
            if (jwtFilter.isAdmin()){
                Optional<User> optionalUser = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if (!optionalUser.isEmpty()){
                    userDao.updateStatus(Integer.parseInt(requestMap.get("id")), requestMap.get("status"));
                    sendMailToAllAdmins(requestMap.get("status"), optionalUser.get().getEmail(),
                            userDao.getAllAdmins());
                    return CafeUtils.getResponseEntity(CafeConstants.USER_STATUS_UPDATE_SUCCESS, HttpStatus.OK);
                } else
                    return CafeUtils.getResponseEntity(CafeConstants.USER_ID_NOT_EXIST, HttpStatus.OK);
            } else
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmins(String status, String user, List<String> allAdmins) {
        allAdmins.remove(user);
        if (status!=null && status.equalsIgnoreCase("true")) {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "USER : " +
                    user + "\n is approved by \nADMIN : " + jwtFilter.getCurrentUser(), allAdmins);
        } else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled", "USER : " +
                    user + "\n is disabled by \nADMIN : " + jwtFilter.getCurrentUser(), allAdmins);

        }
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try{
            User currUser = userDao.findByEmailId(jwtFilter.getCurrentUser());
            if(!Objects.isNull(currUser)) {
                if (currUser.getPassword().equals(requestMap.get("oldPassword"))){
                    currUser.setPassword(requestMap.get("newPassword"));
                    userDao.save(currUser);
                    return CafeUtils.getResponseEntity(CafeConstants.PASSWORD_UPDATE_SUCCESS, HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.INCORRECT_OLD_PASSWORD, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User userObj = userDao.findByEmailId(requestMap.get("email"));
            if (!Objects.isNull(userObj) && !Strings.isNullOrEmpty(userObj.getEmail()))
                emailUtils.forgotPasswordMail(userObj.getEmail(), CafeConstants.FORGOT_PASSWORD_MAIL_SUBJECT, userObj.getPassword());
            return CafeUtils.getResponseEntity(CafeConstants.FORGOT_PASSWORD_CHECK_MAIL, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
