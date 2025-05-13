package com.gp.aiActuator.Engine;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface NameInMap {
    String value();

    String[] alternate() default {};
}