package com.train.springboot.springboot.controller;

import com.train.springboot.springboot.exception.UserNotExistException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author yidong
 * @create 2019-02-03 23:46
 */
@Controller
public class HelloController {
    /*
    @RequestMapping({"/","/index.html"})
    public String index() {
        return "index";
    }
    */

    @ResponseBody
    @RequestMapping("/hello")
    public String hello(@RequestParam("user") String user){
        if(user.equals("aaa")){
           throw new UserNotExistException();
        }

        return "hello world!";
    }
}
