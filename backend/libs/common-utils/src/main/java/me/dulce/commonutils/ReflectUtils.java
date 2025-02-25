package me.dulce.commonutils;

import org.apache.commons.lang3.reflect.InheritanceUtils;

public class ReflectUtils {

    /**
     * Checks if a class is a child class to a parent class
     *
     * @param classToCheck the child class to check
     * @param parentClass the parent class to check
     * @return true if classToCheck is a child of parentClass
     */
    public static boolean isClassChildOfParent(Class<?> classToCheck, Class<?> parentClass) {
        return InheritanceUtils.distance(classToCheck, parentClass) != -1;
    }
}
