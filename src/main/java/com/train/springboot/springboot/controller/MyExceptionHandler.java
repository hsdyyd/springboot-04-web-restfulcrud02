package com.train.springboot.springboot.controller;

import com.train.springboot.springboot.exception.UserNotExistException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yidong
 * @create 2019-02-18-16:49
 */

@ControllerAdvice
public class MyExceptionHandler {
    /*
    @ResponseBody
    @ExceptionHandler(UserNotExistException.class)
    public Map<String,Object> handlerException(Exception e){
        Map<String,Object> map = new HashMap<>();
        map.put("code","user not exist");
        map.put("message",e.getMessage());
        return map;
    }
    */

    @ExceptionHandler(UserNotExistException.class)
    public String handlerException(Exception e, HttpServletRequest request){
        e.printStackTrace();
        Map<String,Object> map = new HashMap<>();
        request.setAttribute("javax.servlet.error.status_code",500);
        map.put("code","user not exist");
        map.put("message",e.getMessage());
        return "/error";
    }

}
