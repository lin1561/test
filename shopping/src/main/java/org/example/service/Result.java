package org.example.service;

import java.util.HashMap;
import java.util.Map;

//数据传输对象，简化数据传输编写并提高效率
public class Result {
    private boolean success;
    private String message;
    private final Map<String, Object> data = new HashMap<>();
    public boolean isSuccess(){
        return success;
    }
    public String getMessage(){ return message;}
    public Map<String, Object> getData(){
        return data;
    }
    public static Result ok(){
        Result result = new Result();
        result.success = true;
        result.message = "操作成功";
        return result;
    }

    public static Result error(){
        Result result = new Result();
        result.success = false;
        result.message = "操作失败";
        return result;
    }

    public Result data(String key, Object value){
        this.data.put(key, value);
        return this;
    }
    public Result message(String message){
        this.message = message;
        return this;
    }
}