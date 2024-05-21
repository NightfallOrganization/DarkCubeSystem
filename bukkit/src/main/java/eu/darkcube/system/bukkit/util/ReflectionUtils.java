/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import eu.darkcube.system.annotations.Api;
import org.bukkit.Bukkit;

/**
 * <b>ReflectionUtils</b>
 * <p>
 * This class provides useful methods which makes dealing with reflection much easier, especially
 * when working with Bukkit
 * <p>
 * You are welcome to use it, modify it and redistribute it under the following conditions:
 * <ul>
 * <li>Don't claim this class as your own
 * <li>Don't remove this disclaimer
 * </ul>
 * <p>
 * <i>It would be nice if you provide credit to me if you use this class in a
 * published project</i>
 *
 * @author DarkBlade12
 * @author DasBabyPixel
 * @version 1.1
 */
@Api
public final class ReflectionUtils {

    // Prevent accidental construction
    private ReflectionUtils() {
    }

    /**
     * Returns the constructor of a given class with the given parameter types
     *
     * @param clazz          Target class
     * @param parameterTypes Parameter types of the desired constructor
     * @param <T>            the data type
     * @return The constructor of the target class with the specified parameter types
     * @throws ReflectionException <br><b>NoSuchMethodException</b> If the desired constructor with
     *                             the specified parameter types cannot be found
     */
    @Api
    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (Exception ex) {
            throw new ReflectionException(ex);
        }
    }

    /**
     * Returns the constructor of a desired class with the given parameter types
     *
     * @param className      Name of the desired target class
     * @param packageType    Package where the desired target class is located
     * @param parameterTypes Parameter types of the desired constructor
     * @param <T>            the data type
     * @return The constructor of the desired target class with the specified parameter types
     * @throws ReflectionException <br><b>NoSuchMethodException</b> If the desired constructor with
     *                             the specified parameter types cannot be found
     *                             <br><b>ClassNotFoundException</b>
     *                             If the desired target class with the specified name and package
     *                             cannot be found
     * @see #getClass(String, PackageType)
     * @see #getConstructor(Class, Class...)
     */
    @Api
    public static <T> Constructor<T> getConstructor(String className, PackageType packageType, Class<?>... parameterTypes) {
        return ReflectionUtils.getConstructor(packageType.getClass(className), parameterTypes);
    }

    /**
     * Creates a new instance of the given constructor with the arguments
     *
     * @param constructor the constructor to use
     * @param arguments   the arguments for the constructor
     * @param <T>         the data type
     * @return the new object created with the constructor
     */
    @Api
    public static <T> T instantiateObject(Constructor<T> constructor, Object... arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ReflectionException(ex);
        }
    }

    /**
     * Instantiates the object of type cls with the default constructor
     *
     * @param cls the class type
     * @param <T> the data type
     * @return the new object
     */
    public static <T> T instantiateObject(Class<T> cls) {
        return instantiateObject(getConstructor(cls));
    }

    /**
     * Returns a method of a class with the given parameter types
     *
     * @param clazz          Target class
     * @param methodName     Name of the desired method
     * @param parameterTypes Parameter types of the desired method
     * @return The method of the target class with the specified name and parameter types
     * @throws ReflectionException If the desired method of the target class with the specified name
     *                             and parameter types cannot be found
     */
    @Api
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (Exception ex) {
            throw new ReflectionException(ex);
        }
    }

    /**
     * Returns a method of a desired class with the given parameter types
     *
     * @param className      Name of the desired target class
     * @param packageType    Package where the desired target class is located
     * @param methodName     Name of the desired method
     * @param parameterTypes Parameter types of the desired method
     * @return The method of the desired target class with the specified name and parameter types
     * @throws ReflectionException <br><b>NoSuchMethodException</b> If the desired method of the
     *                             desired target class with the specified name and parameter types
     *                             cannot be found
     *                             <br><b>ClassNotFoundException</b> If the desired target class
     *                             with the specified name and package cannot be found
     * @see #getClass(String, PackageType)
     * @see #getMethod(Class, String, Class...)
     */
    @Api
    public static Method getMethod(String className, PackageType packageType, String methodName, Class<?>... parameterTypes) {
        return ReflectionUtils.getMethod(packageType.getClass(className), methodName, parameterTypes);
    }

    /**
     * @param className   the class name
     * @param packageType the package to look in
     * @return the class found in the package
     */
    @Api
    public static Class<?> getClass(String className, PackageType packageType) {
        return packageType.getClass(className);
    }

    /**
     * @param className the class name
     * @return the class
     */
    @Api
    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new ReflectionException(ex);
        }
    }

    /**
     * Invokes a method
     *
     * @param instance  the object instance to execute this method on
     * @param method    the method to execute
     * @param arguments the arguments for the method
     * @return the object returned by the method
     */
    @Api
    public static Object invokeMethod(Object instance, Method method, Object... arguments) {
        try {
            return method.invoke(instance, arguments);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ReflectionException(ex);
        }
    }

    /**
     * Returns a field of the target class with the given name
     *
     * @param clazz     Target class
     * @param declared  Whether the desired field is declared or not
     * @param fieldName Name of the desired field
     * @return The field of the target class with the specified name
     * @throws ReflectionException <br><b>NoSuchFieldException</b> If the desired field of the
     *                             given class cannot be found
     *                             <br><b>SecurityException</b> If the desired field cannot be made
     *                             accessible
     */
    @Api
    public static Field getField(Class<?> clazz, boolean declared, String fieldName) {
        try {
            Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException | SecurityException ex) {
            throw new ReflectionException(ex);
        }
    }

    /**
     * Returns a field of a desired class with the given name
     *
     * @param className   Name of the desired target class
     * @param packageType Package where the desired target class is located
     * @param declared    Whether the desired field is declared or not
     * @param fieldName   Name of the desired field
     * @return The field of the desired target class with the specified name
     * @throws ReflectionException <br><b>NoSuchFieldException</b> If the desired field of the
     *                             desired class cannot be found
     *                             <br><b>SecurityException</b> If the desired field cannot be made
     *                             accessible
     *                             <br><b> ClassNotFoundException</b> If the desired target class
     *                             with the specified name and package cannot be found
     * @see #getField(Class, boolean, String)
     */
    @Api
    public static Field getField(String className, PackageType packageType, boolean declared, String fieldName) {
        return ReflectionUtils.getField(packageType.getClass(className), declared, fieldName);
    }

    /**
     * Returns the value of a field of the given class of an object
     *
     * @param instance  Target object
     * @param clazz     Target class
     * @param declared  Whether the desired field is declared or not
     * @param fieldName Name of the desired field
     * @return The value of field of the target object
     * @throws ReflectionException <br><b>IllegalArgumentException</b> If the target object does
     *                             not feature the desired field
     *                             <br><b>IllegalAccessException</b> If the desired field cannot be
     *                             accessed
     *                             <br><b>NoSuchFieldException</b> If the desired field of the
     *                             target class cannot be found
     *                             <br><b>SecurityException</b> If the desired field cannot be made
     *                             accessible
     * @see #getField(Class, boolean, String)
     */
    @Api
    public static Object getValue(Object instance, Class<?> clazz, boolean declared, String fieldName) {
        try {
            return ReflectionUtils.getField(clazz, declared, fieldName).get(instance);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new ReflectionException(ex);
        }
    }

    /**
     * Returns the value of a field of the given class of an object
     *
     * @param instance   Target object
     * @param clazz      Target class
     * @param declared   Whether the desired field is declared or not
     * @param fieldName  Name of the desired field
     * @param valueClass Value class
     * @return The value of field of the target object
     * @throws ReflectionException <br><b>IllegalArgumentException</b> If the target object does
     *                             not feature the desired field
     *                             <br><b>IllegalAccessException</b> If the desired field cannot be
     *                             accessed
     *                             <br><b>NoSuchFieldException</b> If the desired field of the
     *                             target class cannot be found
     *                             <br><b>SecurityException</b> If the desired field cannot be made
     *                             accessible
     *                             <br><b>ClassCastException</b> If the value is not of instance {@code valueClass}
     * @see #getField(Class, boolean, String)
     */
    @Api
    public static <T> T getValue(Object instance, Class<?> clazz, boolean declared, String fieldName, Class<T> valueClass) {
        try {
            return valueClass.cast(ReflectionUtils.getField(clazz, declared, fieldName).get(instance));
        } catch (IllegalArgumentException | IllegalAccessException | ClassCastException ex) {
            throw new ReflectionException(ex);
        }
    }

    /**
     * @param instance the object instance
     * @param field    the field
     * @return the value of the field
     */
    @Api
    public static Object getValue(Object instance, Field field) {
        try {
            return field.get(instance);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new ReflectionException(ex);
        }
    }

    /**
     * Finds a field on the object instance and gets its value
     *
     * @param instance  the object instance
     * @param className the class name
     * @param declared  whether the field is private or not
     * @param fieldName the field name
     * @return the field value
     */
    @Api
    public static Object getValue(Object instance, String className, boolean declared, String fieldName) {
        return ReflectionUtils.getValue(instance, ReflectionUtils.getField(ReflectionUtils.getClass(className), declared, fieldName));
    }

    /**
     * Returns the value of a field of a desired class of an object
     *
     * @param instance    Target object
     * @param className   Name of the desired target class
     * @param packageType Package where the desired target class is located
     * @param declared    Whether the desired field is declared or not
     * @param fieldName   Name of the desired field
     * @return The value of field of the target object
     * @throws ReflectionException <br><b>IllegalArgumentException</b> If the target object does
     *                             not feature the desired field
     *                             <br><b>IllegalAccessException</b> If the desired field cannot be
     *                             accessed
     *                             <br><b>NoSuchFieldException</b> If the desired field of the
     *                             desired class cannot be found
     *                             <br><b>SecurityException</b> If the desired field cannot be made
     *                             accessible
     *                             <br><b>ClassNotFoundException</b> If the desired target class
     *                             with the specified name and package cannot be found
     * @see #getValue(Object, Class, boolean, String)
     */
    @Api
    public static Object getValue(Object instance, String className, PackageType packageType, boolean declared, String fieldName) {
        return ReflectionUtils.getValue(instance, packageType.getClass(className), declared, fieldName);
    }

    /**
     * Returns the value of a field with the given name of an object
     *
     * @param instance  Target object
     * @param declared  Whether the desired field is declared or not
     * @param fieldName Name of the desired field
     * @return The value of field of the target object
     * @throws ReflectionException <br><b>IllegalArgumentException</b> If the target object does
     *                             not feature the desired field (should not occur since it searches
     *                             for a field with the given name in the class of the object)
     *                             <br><b>IllegalAccessException</b> If the desired field cannot be
     *                             accessed
     *                             <br><b>NoSuchFieldException</b> If the desired field of the
     *                             target object cannot be found
     *                             <br><b>SecurityException</b> If the desired field cannot be made
     *                             accessible
     * @see #getValue(Object, Class, boolean, String)
     */
    @Api
    public static Object getValue(Object instance, boolean declared, String fieldName) {
        return ReflectionUtils.getValue(instance, instance.getClass(), declared, fieldName);
    }

    /**
     * Sets the value of a field of the given class of an object
     *
     * @param instance  Target object
     * @param clazz     Target class
     * @param declared  Whether the desired field is declared or not
     * @param fieldName Name of the desired field
     * @param value     New value
     * @throws ReflectionException <br><b>IllegalArgumentException</b> If the type of the value
     *                             does not match the type of the desired field
     *                             <br><b>IllegalAccessException</b> If the desired field cannot be
     *                             accessed
     *                             <br><b>NoSuchFieldException</b> If the desired field of the
     *                             target class cannot be found
     *                             <br><b>SecurityException</b> If the desired field cannot be made
     *                             accessible
     * @see #getField(Class, boolean, String)
     */
    @Api
    public static void setValue(Object instance, Class<?> clazz, boolean declared, String fieldName, Object value) {
        try {
            ReflectionUtils.getField(clazz, declared, fieldName).set(instance, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new ReflectionException(ex);
        }
    }

    /**
     * Sets the value for a field
     *
     * @param instance the object instance
     * @param field    the field
     * @param value    the new value
     */
    @Api
    public static void setValue(Object instance, Field field, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new ReflectionException(ex);
        }
    }

    /**
     * Sets the value of a field of a desired class of an object
     *
     * @param instance    Target object
     * @param className   Name of the desired target class
     * @param packageType Package where the desired target class is located
     * @param declared    Whether the desired field is declared or not
     * @param fieldName   Name of the desired field
     * @param value       New value
     * @throws ReflectionException <br><b>IllegalArgumentException</b> If the type of the value
     *                             does not match the type of the desired field
     *                             <br><b>IllegalAccessException</b> If the desired field cannot be
     *                             accessed
     *                             <br><b>NoSuchFieldException</b> If the desired field of the
     *                             desired class cannot be found
     *                             <br><b>SecurityException</b> If the desired field cannot be made
     *                             accessible
     *                             <br><b>ClassNotFoundException</b> If the desired target class
     *                             with the specified name and package cannot be found
     * @see #setValue(Object, Class, boolean, String, Object)
     */
    @Api
    public static void setValue(Object instance, String className, PackageType packageType, boolean declared, String fieldName, Object value) {
        ReflectionUtils.setValue(instance, packageType.getClass(className), declared, fieldName, value);
    }

    /**
     * Sets the value of a field with the given name of an object
     *
     * @param instance  Target object
     * @param declared  Whether the desired field is declared or not
     * @param fieldName Name of the desired field
     * @param value     New value
     * @throws ReflectionException <br><b>IllegalArgumentException</b> If the type of the value
     *                             does not match the type of the desired field
     *                             <br><b>IllegalAccessException</b>   If the desired field cannot
     *                             be accessed
     *                             <br><b>NoSuchFieldException</b>     If the desired field of the
     *                             target object cannot be found
     *                             <br><b>SecurityException</b>        If the desired field cannot
     *                             be made accessible
     * @see #setValue(Object, Class, boolean, String, Object)
     */
    @Api
    public static void setValue(Object instance, boolean declared, String fieldName, Object value) {
        ReflectionUtils.setValue(instance, instance.getClass(), declared, fieldName, value);
    }

    /**
     * Represents an enumeration of dynamic packages of NMS and CraftBukkit
     * <p>
     * This class is part of the <b>ReflectionUtils</b> and follows the same usage conditions
     *
     * @author DarkBlade12
     * @since 1.0
     */
    public enum PackageType {

        /**
         * net.minecraft.server
         */
        MINECRAFT_SERVER("net.minecraft.server." + PackageType.getServerVersion()),
        /**
         * org.bukkit.craftbukkit
         */
        CRAFTBUKKIT("org.bukkit.craftbukkit." + PackageType.getServerVersion()),
        /**
         * org.bukkit.craftbukkit.block
         */
        CRAFTBUKKIT_BLOCK(CRAFTBUKKIT, "block"),
        /**
         * org.bukkit.craftbukkit.chunkio
         */
        CRAFTBUKKIT_CHUNKIO(CRAFTBUKKIT, "chunkio"),
        /**
         * org.bukkit.craftbukkit.command
         */
        CRAFTBUKKIT_COMMAND(CRAFTBUKKIT, "command"),
        /**
         * org.bukkit.craftbukkit.conversations
         */
        CRAFTBUKKIT_CONVERSATIONS(CRAFTBUKKIT, "conversations"),
        /**
         * org.bukkit.craftbukkit.enchantments
         */
        CRAFTBUKKIT_ENCHANTMENS(CRAFTBUKKIT, "enchantments"),
        /**
         * org.bukkit.craftbukkit.entity
         */
        CRAFTBUKKIT_ENTITY(CRAFTBUKKIT, "entity"),
        /**
         * org.bukkit.craftbukkit.event
         */
        CRAFTBUKKIT_EVENT(CRAFTBUKKIT, "event"),
        /**
         * org.bukkit.craftbukkit.generator
         */
        CRAFTBUKKIT_GENERATOR(CRAFTBUKKIT, "generator"),
        /**
         * org.bukkit.craftbukkit.help
         */
        CRAFTBUKKIT_HELP(CRAFTBUKKIT, "help"),
        /**
         * org.bukkit.craftbukkit.inventory
         */
        CRAFTBUKKIT_INVENTORY(CRAFTBUKKIT, "inventory"),
        /**
         * org.bukkit.craftbukkit.map
         */
        CRAFTBUKKIT_MAP(CRAFTBUKKIT, "map"),
        /**
         * org.bukkit.craftbukkit.metadata
         */
        CRAFTBUKKIT_METADATA(CRAFTBUKKIT, "metadata"),
        /**
         * org.bukkit.craftbukkit.potion
         */
        CRAFTBUKKIT_POTION(CRAFTBUKKIT, "potion"),
        /**
         * org.bukkit.craftbukkit.projectiles
         */
        CRAFTBUKKIT_PROJECTILES(CRAFTBUKKIT, "projectiles"),
        /**
         * org.bukkit.craftbukkit.scheduler
         */
        CRAFTBUKKIT_SCHEDULER(CRAFTBUKKIT, "scheduler"),
        /**
         * org.bukkit.craftbukkit.scoreboard
         */
        CRAFTBUKKIT_SCOREBOARD(CRAFTBUKKIT, "scoreboard"),
        /**
         * org.bukkit.craftbukkit.updater
         */
        CRAFTBUKKIT_UPDATER(CRAFTBUKKIT, "updater"),
        /**
         * org.bukkit.craftbukkit.util
         */
        CRAFTBUKKIT_UTIL(CRAFTBUKKIT, "util");

        private final String path;

        /**
         * Construct a new package type
         *
         * @param path Path of the package
         */
        PackageType(String path) {
            this.path = path;
        }

        /**
         * Construct a new package type
         *
         * @param parent Parent package of the package
         * @param path   Path of the package
         */
        PackageType(PackageType parent, String path) {
            this(parent + "." + path);
        }

        /**
         * Returns the version of your server
         *
         * @return The server version
         */
        @Api
        public static String getServerVersion() {
            return Bukkit.getServer().getClass().getPackage().getName().substring(23);
        }

        /**
         * Returns the path of this package type
         *
         * @return The path
         */
        @Api
        public String getPath() {
            return this.path;
        }

        /**
         * Returns the class with the given name
         *
         * @param className Name of the desired class
         * @param <T>       the data type
         * @return The class with the specified name
         * @throws ReflectionException <br><b>ClassNotFoundException</b> If the desired class with
         *                             the specified name and package cannot be found
         */
        @Api
        public <T> Class<T> getClass(String className) {
            try {
                return (Class<T>) Class.forName(this + "." + className);
            } catch (ClassNotFoundException ex) {
                throw new ReflectionException(ex);
            }
        }

        // Override for convenience
        @Override
        public String toString() {
            return this.path;
        }
    }

    /**
     * Exception for reflection related operations
     * <br>
     */
    @Api
    public static class ReflectionException extends RuntimeException {

        @Api
        public ReflectionException() {
            super();
        }

        @Api
        public ReflectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }

        @Api
        public ReflectionException(String message, Throwable cause) {
            super(message, cause);
        }

        @Api
        public ReflectionException(String message) {
            super(message);
        }

        @Api
        public ReflectionException(Throwable cause) {
            super(cause);
        }
    }
}
