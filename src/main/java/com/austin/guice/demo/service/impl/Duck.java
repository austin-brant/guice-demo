package com.austin.guice.demo.service.impl;

import com.austin.guice.demo.service.Animal;

/**
 * @author austin
 * @since 2019/11/22 15:44   Fri
 */
public class Duck implements Animal {
    public void talk() {
        System.out.println("i am duck");
    }
}
