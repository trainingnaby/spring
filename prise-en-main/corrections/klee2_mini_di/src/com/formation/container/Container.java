package com.formation.container;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.formation.interfaces.DependanceAInjecter;
import com.formation.interfaces.BeanACreer;
// cette classe est le conteneur d'injection de dépendances
// elle va instancier les classes marquées @BeanACreer
// et injecter les dépendances marquées @DependanceAInjecter
// pour cela elle va utiliser la réflexion de Java
public class Container {
    private Map<Class<?>, Object> container = new HashMap<>();

    public Container(Class<?>... classes) throws Exception {
        // Instanciation des classes marquées @BeanACreer
        for (Class<?> classe : classes) {
            if (classe.isAnnotationPresent(BeanACreer.class)) {
                Object instance = classe.getDeclaredConstructor().newInstance();
                container.put(classe, instance);
            }
        }

        // Injection des dépendances marquées @DependanceAInjecter
        for (Object objet : container.values()) {
            for (Field field : objet.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(DependanceAInjecter.class)) {
                    Class<?> dependencyType = field.getType();
                    Object dependency = container.get(dependencyType);
                    field.setAccessible(true);
                    field.set(objet, dependency);
                }
            }
        }
    }

    public <T> T getBean(Class<T> clazz) {
        return clazz.cast(container.get(clazz));
    }
}
