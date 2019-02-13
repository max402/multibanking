package org.adorsys.multibanking.onlinebanking.mapper.api;

import domain.response.LoadAccountInformationResponse;
import domain.response.LoadBookingsResponse;
import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.cryptoutils.exceptions.BaseExceptionHandler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by peter on 12.02.19 at 23:56.
 */
public class ReflectionTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(ReflectionTest.class);

    @Test
    public void printMembersOfLoadAccountInformationResponse() {
        printMembers("", "LoadAccountInformationResponse",  LoadAccountInformationResponse.class);
    }

    @Test
    public void printMembersLoadBookingsResponse() {
        printMembers("", "LoadBookingsResponse", LoadBookingsResponse.class);
    }


    private void printMembers(String path, String name, Class<?> clazzOfName) {
        if (name.length() > 0) {
            path = path + " " + name;
        }
        for (Field field : clazzOfName.getDeclaredFields()) {
            if (isInterface(field.getType())) {
                if (field.getGenericType() instanceof ParameterizedType) {
                    printline(1, path + " " + field.getName(), field.getGenericType().getTypeName());
                    showParameterizedType((ParameterizedType) field.getGenericType(), field.getName() + "<>", path);
                } else {
                    printline(2, path + " " + field.getName(), ((Class) field.getGenericType()).getName());
                    printMembers(path, ((Class) field.getGenericType()).getName(), (Class) field.getGenericType());
                }
            } else {
                showField(field.getName(), field.getType(), path);
            }
        }
    }


    private void showParameterizedType(ParameterizedType parameterizedType, String name, String path) {
        if (name.length() > 0) {
            path = path + " " + name;
        }
        int numberOfTemplateParams = parameterizedType.getActualTypeArguments().length;
        for (int param = 0; param < numberOfTemplateParams; param++) {
            Type type = parameterizedType.getActualTypeArguments()[param];
            if (type instanceof Class) {
                Class<?> paramClazz = (Class<?>) parameterizedType.getActualTypeArguments()[param];
                showField(paramClazz.getName(), paramClazz, path);
            } else {
                if (isInterface(type)) {
                    throw new BaseException(path + " " + name + " " + type.getTypeName() + " could not continue with interface type");
                } else {
                    if (type instanceof ParameterizedType) {
                        showParameterizedType((ParameterizedType) type, "", path + " " + type.getTypeName());

                    }
                }
            }
        }

    }

    private void showField(String name, Class<?> clazz, String  path) {
        if (name.length() > 0) {
            path = path + " " + name;
        }

        if (clazz.isEnum()) {
            printline(3, path,  "enum");
        } else {
            printline(4, path,  clazz.getTypeName());
            if (clazz.getPackage() == null || clazz.getPackage().getName().startsWith("java")) {
                // ist java class or native
            } else {
                printMembers(path, "", clazz);
            }
        }
    }

    private void printline(int id, String path, String type) {
        LOGGER.info(id + " - " + path + " - " + "(" + type + ")");
    }
    private boolean isInterface(Class<?> clazz) {
        return ("" + clazz).contains("interface");
    }
    private boolean isInterface(Type type) {
        return ("" + type).contains("interface");
    }

}
