package com.rejs.reservation.domain.screening.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import com.rejs.reservation.controller.docs.DocsUtils;
import com.rejs.reservation.domain.movie.controller.docs.MovieDtoDocs;
import com.rejs.reservation.domain.theater.controller.docs.TheaterSummaryDocs;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ScreeningDetailDtoDocs {
    public static Schema getSchema(){
        return new Schema("screeningDetail");
    }

    public static FieldDescriptors getFields(){
        return new FieldDescriptors(
                fieldWithPath("seats[].seatId").description("좌석 고유번호").type(JsonFieldType.NUMBER),
                fieldWithPath("seats[].row").description("좌석의 행번호").type(JsonFieldType.NUMBER),
                fieldWithPath("seats[].col").description("좌석의 열번호").type(JsonFieldType.NUMBER),
                fieldWithPath("seats[].reserved").description("예약된 좌석인지").type(JsonFieldType.BOOLEAN)
        )
                .andWithPrefix("screening.", DocsUtils.mergeFields(ScreeningDtoDocs.fields()))
                .andWithPrefix("movie.", DocsUtils.mergeFields(MovieDtoDocs.fields()))
                .andWithPrefix("theater.",DocsUtils.mergeFields(TheaterSummaryDocs.fields()))
        ;
    }
}
