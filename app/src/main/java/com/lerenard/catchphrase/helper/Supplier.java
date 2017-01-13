package com.lerenard.catchphrase.helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by mc on 18-Dec-16.
 */
public class Supplier<T> {
    private final Constructor<? extends T> ctor;

    public Supplier(Constructor<? extends T> ctor) {
        this.ctor = ctor;
    }

    public T get(Object... args) {
        try {
            return ctor.newInstance(args);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
