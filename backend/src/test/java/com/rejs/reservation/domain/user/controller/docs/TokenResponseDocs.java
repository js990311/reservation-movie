package com.rejs.reservation.domain.user.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class TokenResponseDocs {
    public static Schema schema(){
        return new Schema("tokens");
    }

    public static FieldDescriptors fields(){
        return new FieldDescriptors(
                fieldWithPath("data.email").type(JsonFieldType.STRING).description("로그인한 유저의 email"),
                fieldWithPath("data.roles[]").type(JsonFieldType.ARRAY).description("로그인한 유저의 roles"),
                fieldWithPath("data.tokens").type(JsonFieldType.OBJECT).description("토근정보"),
                fieldWithPath("data.tokens.accessToken").type(JsonFieldType.OBJECT).description("액세스토큰"),
                fieldWithPath("data.tokens.accessToken.token").type(JsonFieldType.STRING).description("액세스토큰 실제 토큰값"),
                fieldWithPath("data.tokens.accessToken.expiresAt").type(JsonFieldType.NUMBER).description("액세스토큰 만료일"),
                fieldWithPath("data.tokens.refreshToken").type(JsonFieldType.OBJECT).description("리프레시 토큰"),
                fieldWithPath("data.tokens.refreshToken.token").type(JsonFieldType.STRING).description("리프레시 토큰 실제 토큰값"),
                fieldWithPath("data.tokens.refreshToken.expiresAt").type(JsonFieldType.NUMBER).description("리프레시 토큰 만료일")
        );
    }
}
