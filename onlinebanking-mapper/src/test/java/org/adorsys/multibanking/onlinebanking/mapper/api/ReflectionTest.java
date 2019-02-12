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
        printMembers("", LoadAccountInformationResponse.class);
    }

    @Test
    public void printMembersLoadBookingsResponse() {
        printMembers("", LoadBookingsResponse.class);
    }


    private void printMembers(String indent, Class<?> clazz) {
        String space = indent + " " + clazz.getName() + " ";
        for (Field field : clazz.getDeclaredFields()) {
            if (("" + field.getType()).contains("interface")) {
                LOGGER.info(1 + space + field.getName() + " (" + field.getGenericType() + ")");
                if (field.getGenericType() instanceof ParameterizedType) {
                    showParameterizedType((ParameterizedType) field.getGenericType(), field, space);
                } else {
                    LOGGER.info("1b " + ((Class) field.getGenericType()).getName());
                    printMembers(indent + " " + field.getGenericType(), (Class) field.getGenericType());
                }
            } else {

                LOGGER.info(5 + space + field.getName() + " (" + field.getType().getName() + ")");

                if (field.getType().isEnum()) {
                    LOGGER.info(6 + space + field.getType().getName() + " (enum)");
                } else {
                    if (field == null) {
                        throw new BaseException("affe");
                    }
                    if (field.getType().getPackage() == null || field.getType().getPackage().getName().startsWith("java")) {
                        // ist java class or native
                    } else {
                        printMembers(space, field.getType());
                    }
                }
            }
        }
    }


    private void showParameterizedType(ParameterizedType parameterizedType, Field field, String space) {
        int numberOfTemplateParams = parameterizedType.getActualTypeArguments().length;
        for (int param = 0; param < numberOfTemplateParams; param++) {
            Type type = parameterizedType.getActualTypeArguments()[param];
            if (type instanceof Class) {
                Class<?> paramClazz = (Class<?>) parameterizedType.getActualTypeArguments()[param];
                if (paramClazz.isEnum()) {
                    LOGGER.info(2 + space + paramClazz.getName() + " (enum)");
                } else {
                    String packaage = paramClazz.getPackage().getName();
                    if (!packaage.startsWith("java")) {
                        printMembers(space, paramClazz);
                    } else {
                        LOGGER.info(3 + space + paramClazz.getName() + " is java lang");
                    }
                }
            } else {
                if (("" + type).contains("interface")) {
                    LOGGER.info(4.5 + space + field.getName() + " " + type.getTypeName() + " could continue with interface type");
                } else {
                    if (type instanceof ParameterizedType) {
                        showParameterizedType((ParameterizedType) type, field, space + " " + type.getTypeName());

                    }
                }
            }
        }

    }

}
