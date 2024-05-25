package com.nonso.bankapp.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nonso.bankapp.filter.CustomCommonsRequestLoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.text.SimpleDateFormat;

import static com.nonso.bankapp.utils.GeneralConstants.DATETIME_FORMAT;
import static com.nonso.bankapp.utils.GeneralConstants.LOCAL_DATE_TIME_SERIALIZER;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    @Value("${api_key}")
    private String apiKey;
    @Value("${api_secret}")
    private String apiSecret;
    @Value("${cloud_name}")
    private String cloudName;

    @Bean("cloudinary")
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    @Bean
    public CommonsRequestLoggingFilter loggingFilter() {
        CommonsRequestLoggingFilter filter = new CustomCommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(100000);
        filter.setIncludeHeaders(true);
        filter.setHeaderPredicate(h -> !h.equalsIgnoreCase(HttpHeaders.AUTHORIZATION));
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        filter.setIncludeClientInfo(true);
        return filter;
    }

    @Bean("bankAppObjectMapper")
    public ObjectMapper objectMapper(@Qualifier("bankAppJavaTimeModule") JavaTimeModule javaTimeModule) {
        return JsonMapper.builder().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .serializationInclusion(JsonInclude.Include.ALWAYS)
                .defaultDateFormat(new SimpleDateFormat(DATETIME_FORMAT))
                .addModule(javaTimeModule).build();
    }

    @Bean("bankAppJavaTimeModule")
    public JavaTimeModule javaTimeModule() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LOCAL_DATE_TIME_SERIALIZER);
        return module;
    }
}
