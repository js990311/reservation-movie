package com.rejs.reservation.domain.user.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class LoginRequestDocs {
    public static Schema schema(){
        return new Schema("loginRequest");
    }

    public static FieldDescriptors fields(){
        return new FieldDescriptors(
                fieldWithPath("username").type(JsonFieldType.STRING).description("아이디"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
        );
    }
}
