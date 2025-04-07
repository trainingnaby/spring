package com.formation.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Retention policy for runtime
@Target(ElementType.FIELD) // Targeting fields
public @interface DependanceAInjecter {
}
