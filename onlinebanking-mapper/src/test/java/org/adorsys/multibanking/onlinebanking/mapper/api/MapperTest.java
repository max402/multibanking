package org.adorsys.multibanking.onlinebanking.mapper.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.adorsys.cryptoutils.exceptions.BaseExceptionHandler;
import org.adorsys.multibanking.onlinebanking.mapper.api.domain.UserData;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by peter on 06.02.19 12:34.
 */
public class MapperTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(MapperTest.class);

    @Test
    public void a() {
        try {
            String content = readFile("structure.example.json");
            LOGGER.info(" before:" + content);

            TypeReference<UserData> rootType = new TypeReference<UserData>() {};
            ObjectMapper om = new ObjectMapper();
//            om.enable(SerializationFeature.INDENT_OUTPUT);

            UserData userData = om.readValue(content, rootType);
            String reloadedContent = om.writerWithDefaultPrettyPrinter().writeValueAsString(userData);

            LOGGER.info(" after: " + reloadedContent);

            int compare = StringUtils.compare(content, reloadedContent);
            LOGGER.info("compare is " + compare);
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
