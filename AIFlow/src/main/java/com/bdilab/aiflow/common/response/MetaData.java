package com.bdilab.aiflow.common.response;

/**
 * @Decription TODO
 * @Author Humphrey  (origin)
 * @Date 2020/9/9
 * @Version 1.1
 * @apiNote add size
 **/
public class MetaData {
    /**
     * 是否成功的标识
     */
    private Boolean success;

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回消息
     */
    private String message;

    private int size;

    public MetaData(){

    }

    public MetaData(boolean success, String code, String message){
        this.success=success;
        this.code=code;
        this.message=message;
    }

    public MetaData(boolean success, String code, String message, int size){
        this.success=success;
        this.code=code;
        this.message=message;
        this.size=size;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}