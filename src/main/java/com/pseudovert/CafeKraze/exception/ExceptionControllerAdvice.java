package com.pseudovert.CafeKraze.exception;

import com.pseudovert.CafeKraze.constants.CafeConstants;
import com.pseudovert.CafeKraze.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler
    ResponseEntity<String> exceptionHandler(Exception ex) {
        ex.printStackTrace();
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
