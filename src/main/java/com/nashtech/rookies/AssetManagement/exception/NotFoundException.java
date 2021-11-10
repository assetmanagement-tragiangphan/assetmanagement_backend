package com.nashtech.rookies.AssetManagement.exception;


public class NotFoundException extends RuntimeException{
    public NotFoundException(Class<?> clazz,Long id) {
        super(clazz.getName().replace("com.nashtech.rookies.AssetManagement.model.entity.","")+" has id="+ id +" not found!");
    }
    public NotFoundException(Class<?> clazz,String id) {
        super(clazz.getName().replace("com.nashtech.rookies.AssetManagement.model.entity.","")+" has id="+ id +" not found!");
    }
    public NotFoundException(String msg){
        super(msg);
    }

}
