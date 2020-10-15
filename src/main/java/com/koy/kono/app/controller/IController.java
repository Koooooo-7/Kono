package com.koy.kono.app.controller;

public interface IController {
    default String name(){
        return "";
    };
}
