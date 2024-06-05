package me.dulce.gamesite.gamesite2.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.file.Paths;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class TestUtils {

    public static String objectToString(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String objectAsArrayToString(Object... objs) {
        return objectToString(objs);
    }

    public static String concatPaths(String first, String... paths) {
        return Paths.get(first, paths).toString().replace('\\', '/');
    }

    public static ResultActions getRequest(
            MockMvc mockMvc, String endpoint, HttpStatus expectedStatus) throws Exception {
        return getRequest(mockMvc, endpoint, new LinkedMultiValueMap<>(), expectedStatus);
    }

    public static ResultActions getRequest(
            MockMvc mockMvc,
            String endpoint,
            MultiValueMap<String, String> params,
            HttpStatus expectedStatus)
            throws Exception {
        return mockMvc.perform(
                        MockMvcRequestBuilders.get(endpoint)
                                .params(params)
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is(expectedStatus.value()));
    }

    public static ResultActions postRequest(
            MockMvc mockMvc, String endpoint, HttpStatus expectedStatus) throws Exception {
        return postRequest(mockMvc, endpoint, new LinkedMultiValueMap<>(), expectedStatus);
    }

    public static ResultActions postRequest(
            MockMvc mockMvc,
            String endpoint,
            MultiValueMap<String, String> params,
            HttpStatus expectedStatus)
            throws Exception {
        return mockMvc.perform(
                        MockMvcRequestBuilders.post(endpoint)
                                .params(params)
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is(expectedStatus.value()));
    }

    public static MultiValueMap<String, String> getMVMapFromString(String... values) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        for (String v : values) {
            String[] parts = v.split("=", 2);
            map.add(parts[0], parts[1]);
        }
        return map;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @WithSecurityUser("basicUser")
    public @interface WithBasicUser {}
}
