package com.rejs.reservation.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.controller.docs.BusinessExceptionDocs;
import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import com.rejs.reservation.global.security.jwt.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest
public class AbstractMockControllerTest {
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext context;

    @MockitoBean
    protected JwtUtils jwtUtils;
    protected MockMvc mockMvc;

    @BeforeEach
    void setUp(final WebApplicationContext context, final RestDocumentationContextProvider restDocumentation){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(MockMvcResultHandlers.print())
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    public RestDocumentationResultHandler document(Function<ResourceSnippetParametersBuilder, ResourceSnippetParametersBuilder> builderFunction){
        return document("/{class-name}/{method-name}", builderFunction);
    }

    public RestDocumentationResultHandler document(String identifier, Function<ResourceSnippetParametersBuilder, ResourceSnippetParametersBuilder> builderFunction){
        return MockMvcRestDocumentationWrapper.document(
                identifier,
                ResourceDocumentation.resource(
                        builderFunction.apply(ResourceSnippetParameters.builder())
                                .build()
                )
        );
    }

    public RestDocumentationResultHandler documentWithException(Function<ResourceSnippetParametersBuilder, ResourceSnippetParametersBuilder> builderFunction){
        return MockMvcRestDocumentationWrapper.document(
                "/{class-name}/{method-name}",
                ResourceDocumentation.resource(
                        builderFunction.apply(
                                        ResourceSnippetParameters.builder()
                                                .responseSchema(BusinessExceptionDocs.schema())
                                                .responseFields(BusinessExceptionDocs.fields())
                                )
                                .build()
                )
        );
    }

    public void andExpectException(Supplier<ResultActions> supplier, BusinessExceptionCode code, String instance) throws Exception {
        ResultActions result = supplier.get();

        result
                .andExpect(status().is(code.getStatus().value()))
                .andExpect(jsonPath("$.error.type").isString())
                .andExpect(jsonPath("$.error.type").value(code.getType()))
                .andExpect(jsonPath("$.error.title").isString())
                .andExpect(jsonPath("$.error.title").value(code.getTitle()))
                .andExpect(jsonPath("$.error.status").isNumber())
                .andExpect(jsonPath("$.error.status").value(code.getStatus().value()))
                .andExpect(jsonPath("$.error.instance").isString())
                .andExpect(jsonPath("$.error.instance").value(instance))
                .andExpect(jsonPath("$.error.detail").isString())
        ;
    }



}
