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

            LoadAccountInformationResponse response;
            SimpleMockBanking simpleMockBanking = new SimpleMockBanking(null, null);
            BankAccess bankAccess = new BankAccess();
            bankAccess.setBankLogin(user);

            response = simpleMockBanking.loadBankAccounts(
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

            UserData userData = new UserData();
            ObjectMapper om = new ObjectMapper();
            LOGGER.debug("userData before call:" + om.writerWithDefaultPrettyPrinter().writeValueAsString(userData));

            Mapper mapper = new Mapper();
            userData = mapper.merge(userData, response);

            LOGGER.debug("userData after call:" + om.writerWithDefaultPrettyPrinter().writeValueAsString(userData));
        } catch (Exception e) {
            throw BaseExceptionHandler.handle(e);
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
