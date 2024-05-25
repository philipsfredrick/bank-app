package com.nonso.bankapp.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nonso.bankapp.exception.BankAppException;


import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class TestUtil {

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getJsonObject(String response, Class<T> clazz) {
        T proceedResponse;
        try {
            proceedResponse = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false)
                    .readValue(response, clazz);
        } catch (Exception e) {
            throw new BankAppException("An error occurred", INTERNAL_SERVER_ERROR);
        }
        return proceedResponse;
    }

    public static <T> List<T> getListFromJsonObject(String response, Class<T> clazz) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            var tree = new ObjectMapper().readTree(response);
            var list = new ArrayList<T>();
            for (JsonNode jsonNode : tree) {
                list.add(objectMapper.treeToValue(jsonNode, clazz));
            }
            return list;
        } catch (Exception e) {
            throw new BankAppException("An error occurred", INTERNAL_SERVER_ERROR);
        }
    }
}
