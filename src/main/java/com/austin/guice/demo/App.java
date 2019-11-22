package com.austin.guice.demo;

import com.austin.guice.demo.config.AnimalModule;
import com.austin.guice.demo.service.AnimalService;
import com.austin.guice.demo.service.UserService;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author austin
 * @since 2019/11/22 15:36   Fri
 */
public class App {

    public static void main(String[] args) {
        final Injector injector = Guice.createInjector(new AnimalModule());
        //        final Injector injector = Guice.createInjector();
        final UserService userService = injector.getInstance(UserService.class);
        userService.say();
        final AnimalService animalService = injector.getInstance(AnimalService.class);
        animalService.talk();
    }
}
