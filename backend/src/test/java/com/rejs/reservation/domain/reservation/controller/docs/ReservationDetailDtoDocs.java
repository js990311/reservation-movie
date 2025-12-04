package com.rejs.reservation.domain.reservation.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import com.rejs.reservation.controller.docs.DocsUtils;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ReservationDetailDtoDocs {
    public static Schema schema(){
        return new Schema("reservationDetail");
    }

    public static FieldDescriptors fields(){
        return new FieldDescriptors(
                fieldWithPath("reservation").type(JsonFieldType.OBJECT).description("예매에 대한 개략정보"),
                fieldWithPath("seats[]").type(JsonFieldType.ARRAY).description("예매한 좌석들")
        )
                .andWithPrefix("reservation.", DocsUtils.mergeFields(ReservationSummaryDtoDocs.fields()))
                .andWithPrefix("seats[].", DocsUtils.mergeFields(seatFields()));
    }

    public static FieldDescriptors seatFields(){
        return new FieldDescriptors(
                fieldWithPath("row").type(JsonFieldType.NUMBER).description("좌석의 행 번호"),
                fieldWithPath("col").type(JsonFieldType.NUMBER).description("좌석의 열 번호")
        );
    }
}
