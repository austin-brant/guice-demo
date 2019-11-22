package com.austin.guice.demo.service;

import com.austin.guice.demo.annotation.DuckAnnotation;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author austin
 * @since 2019/11/22 15:45   Fri
 */
public class AnimalService {

    @Inject
    @Named(value = "timeout")
    private int number;

    @Inject
    @Named(value = "JDBC URL")
    private String jdbcUrl;

    @Inject
    private Animal dog;

    @Inject
    @DuckAnnotation
    private Animal duck;

    public void talk() {
        System.out.println(number);
        System.out.println(jdbcUrl);
        System.out.println("dog will talk");
        dog.talk();
        System.out.println("duck will talk");
        duck.talk();
    }
}
