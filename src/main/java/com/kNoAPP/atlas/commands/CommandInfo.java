package com.kNoAPP.atlas.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {

	String name();
    String[] aliases() default {};
    String description() default "";
    String usage() default "";
    String permission() default "";
    int[] length() default {-1};
    int argMatch() default -1;
}
