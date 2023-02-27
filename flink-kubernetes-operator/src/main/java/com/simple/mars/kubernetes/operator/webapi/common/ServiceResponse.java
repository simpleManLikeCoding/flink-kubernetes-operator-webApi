package com.simple.mars.kubernetes.operator.webapi.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Map;


@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)//保证序列化JSON的时候，NULL的时候，KEY消失
public class ServiceResponse<T> implements Serializable {

    private String msg;
    private int code;
    private T data;

    private ServiceResponse(int code,String msg)
    {
        this.code = code;
        this.msg = msg;
    }

    private ServiceResponse(int code,String msg,T data)
    {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    public String getMsg(){
        return this.msg;
    }


    public int getCode(){
        return this.code;
    }

    public T getData(){
        return this.data;
    }


    public static <T> ServiceResponse<T> createBySuccess(String msg,int code) {
        return new ServiceResponse<T>(code,msg);
    }
    public static <T> ServiceResponse<T> createByError(String msg,int code) {
        return new ServiceResponse<T>(code,msg);
    }

    public static <T> ServiceResponse<T> createBySuccess(String msg,int code,T data) {
        return new ServiceResponse<T>(code,msg,data);
    }


}
