package com.train.springboot.springboot.exception;

/**
 * @author yidong
 * @create 2019-02-18-16:53
 */

public class UserNotExistException extends RuntimeException{
    public UserNotExistException(){
        super("用户不存在");
    }
}
