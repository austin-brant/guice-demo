package com.austin.guice.demo.config;

import com.austin.guice.demo.annotation.DuckAnnotation;
import com.austin.guice.demo.service.Animal;
import com.austin.guice.demo.service.impl.Dog;
import com.austin.guice.demo.service.impl.Duck;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * @author austin
 * @since 2019/11/22 15:49   Fri
 */
public class AnimalModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Animal.class).to(Dog.class); // 这行的意思即是把Dog绑定给IAnimal
        bind(Animal.class).annotatedWith(DuckAnnotation.class).to(Duck.class);
        bind(String.class)
                .annotatedWith(Names.named("JDBC URL"))
                .toInstance("jdbc:mysql://localhost/pizza");

        bind(Integer.class)
                .annotatedWith(Names.named("timeout"))
                .toInstance(10);
    }
}
