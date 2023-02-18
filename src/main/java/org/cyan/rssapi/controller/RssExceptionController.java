package org.cyan.rssapi.controller;


import lombok.extern.slf4j.Slf4j;
import org.cyan.rssapi.exceptions.UrlsArgumentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class RssExceptionController {

    @ExceptionHandler(value = UrlsArgumentException.class)
    public ResponseEntity<Object> exception(UrlsArgumentException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
