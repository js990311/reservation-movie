package com.rejs.reservation.domain.reservation.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ReservationRequestDocs {
    public static Schema schema(){
        return new Schema("reservationRequest");
    }

    public static FieldDescriptors fields(){
        return new FieldDescriptors(
            fieldWithPath("screeningId").type(JsonFieldType.NUMBER).description("상영표 id"),
            fieldWithPath("seats[]").type(JsonFieldType.ARRAY).description("예매할 좌석들")
        );
    }
}
