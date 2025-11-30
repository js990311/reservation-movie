package com.rejs.reservation.domain.reservation.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import com.rejs.reservation.controller.docs.DocsUtils;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ReservationDtoDocs {
    public static Schema schema(){
        return new Schema("reservation");
    }

    public static FieldDescriptors fields(){
        return new FieldDescriptors(
                fieldWithPath("reservationId").type(JsonFieldType.NUMBER).description("상영표 id"),
                fieldWithPath("status").type(JsonFieldType.STRING).description("예매 상태"),
                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("예매한 유저번호 id"),
                fieldWithPath("screeningId").type(JsonFieldType.NUMBER).description("상영표 고유번호"),
                fieldWithPath("reservationSeats").type(JsonFieldType.ARRAY).description("예매한 좌석들")
        ).andWithPrefix("reservationSeats[].", DocsUtils.mergeFields(seats()));
    }

    private static FieldDescriptors seats(){
        return new FieldDescriptors(
                fieldWithPath("reservationSeatId").type(JsonFieldType.NUMBER).description("예매 좌석번호"),
                fieldWithPath("seatId").type(JsonFieldType.NUMBER).description("좌석번호"),
                fieldWithPath("reservationId").type(JsonFieldType.NUMBER).description("예매번호")
        );
    }

}
