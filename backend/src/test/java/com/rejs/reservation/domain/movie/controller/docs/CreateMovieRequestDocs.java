package com.rejs.reservation.domain.movie.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CreateMovieRequestDocs {
    public static Schema schema(){
        return new Schema("createMovieRequest");
    }

    public static FieldDescriptors fields(){
        return new FieldDescriptors(
            fieldWithPath("title").description("영화제목").type(JsonFieldType.STRING),
            fieldWithPath("duration").description("상영시간").type(JsonFieldType.NUMBER)
        );
    }
}
