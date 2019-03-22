package com.fundcount.currencyexchange;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {
    public static void main(String[] args) {
        //инициализация контекста приложения
        new ClassPathXmlApplicationContext("classpath:fundcount-currencyexchange-config.xml");
    }
}
