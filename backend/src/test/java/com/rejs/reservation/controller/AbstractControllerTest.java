package com.rejs.reservation.controller;

import com.epages.restdocs.apispec.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.controller.docs.BusinessExceptionDocs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.function.Function;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
public class AbstractControllerTest {
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext context;

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


    public HeaderDescriptorWithType authorizationHeader(){
        return new HeaderDescriptorWithType("Authorization").description("액세스토큰");
    }
}
