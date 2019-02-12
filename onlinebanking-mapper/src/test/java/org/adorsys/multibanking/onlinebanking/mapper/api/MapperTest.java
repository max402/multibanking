package org.adorsys.multibanking.onlinebanking.mapper.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.BankAccess;
import domain.request.LoadAccountInformationRequest;
import domain.response.LoadAccountInformationResponse;
import org.adorsys.cryptoutils.exceptions.BaseExceptionHandler;
import org.adorsys.multibanking.onlinebanking.facade.mock.SimpleMockBanking;
import org.adorsys.multibanking.onlinebanking.mapper.api.domain.UserData;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by peter on 06.02.19 12:34.
 */
public class MapperTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(MapperTest.class);

    @Test
    public void testPlainReadWrite() {
        try {
            String content = readFile("structure.example.json");
            LOGGER.debug(" before:" + content);

            TypeReference<UserData> rootType = new TypeReference<UserData>() {
            };
            ObjectMapper om = new ObjectMapper();
            UserData userData = om.readValue(content, rootType);
            String reloadedContent = om.writerWithDefaultPrettyPrinter().writeValueAsString(userData);

            LOGGER.debug(" after: " + reloadedContent);

            Assert.assertEquals(0, StringUtils.compare(content, reloadedContent));
        } catch (Exception e) {
            throw BaseExceptionHandler.handle(e);
        }

    }

    @Test
    public void testLoadAccounts() {
        try {
            String user = "p.spiessbach";
            String pin = "11111";

            SimpleMockBanking simpleMockBanking = new SimpleMockBanking(null, null);
            BankAccess bankAccess = new BankAccess();
            bankAccess.setBankLogin(user);

            LoadAccountInformationResponse response = simpleMockBanking.loadBankAccounts(
                    null,
                    LoadAccountInformationRequest.builder()
                            .bankApiUser(null)
                            .bankAccess(bankAccess)
                            .bankCode(null)
                            .pin(pin)
                            .storePin(false)
                            .updateTanTransportTypes(true)
                            .build()
            );

            BackendUserData userData = new BackendUserData();
            userData.setSomeBackendInfo("someBackend info unknown2");
            ObjectMapper om = new ObjectMapper();
            LOGGER.debug("userData before call:" + om.writerWithDefaultPrettyPrinter().writeValueAsString(userData));

            Mapper mapper = new Mapper();
            userData = (BackendUserData) mapper.merge(userData, response);

            LOGGER.debug("userData after call:" + om.writerWithDefaultPrettyPrinter().writeValueAsString(userData));
        } catch (Exception e) {
            throw BaseExceptionHandler.handle(e);
        }

    }

    @Test
    public void printMembers() {
        printMembers("", LoadAccountInformationResponse.class);
    }

    private void printMembers(String indent, Class<?> clazz) {
        String space = indent + " " + clazz.getName() + " ";
        for (Field field : clazz.getDeclaredFields()) {
            if (("" + field.getType()).contains("interface")) {
                LOGGER.info(1 + space + field.getName() + " (" + field.getGenericType() + ")");
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
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
                        LOGGER.info(4 + space + field.getName() + " " + type.getTypeName() + " ??? DONT KNOW WHAT TO DO");
                    }
                }
            } else {

                LOGGER.info(5 + space + field.getName() + " (" + field.getType().getName() + ")");

                if (field.getType().isEnum()) {
                    LOGGER.info(6 + space + field.getType().getName() + " (enum)");
                } else {
                    String packaage = field.getType().getPackage().getName();
                    if (!packaage.startsWith("java")) {
                        printMembers(space, field.getType());
                    } else {
                        // LOGGER.info(7 + space + field.getName() + " is java lang");
                    }
                }
            }
        }
    }


    private String readFile(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            return IOUtils.toString(classLoader.getResourceAsStream(fileName));
        } catch (Exception e) {
            throw BaseExceptionHandler.handle(e);
        }
    }
}
