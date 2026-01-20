package com.rejs.reservation.domain.user.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class RefreshRequestDocs {
    public static Schema schema(){
        return new Schema("refreshRequest");
    }

    public static FieldDescriptors fields(){
        return new FieldDescriptors(
                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시토큰")
        );
    }

}
