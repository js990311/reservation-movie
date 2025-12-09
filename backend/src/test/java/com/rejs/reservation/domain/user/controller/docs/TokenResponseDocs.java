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
                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("액세스토큰"),
                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
        );
    }
}
