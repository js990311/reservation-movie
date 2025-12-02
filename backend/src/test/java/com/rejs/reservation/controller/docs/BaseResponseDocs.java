package com.rejs.reservation.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class BaseResponseDocs {
    public static FieldDescriptors baseFields(FieldDescriptors field){
        return new FieldDescriptors(
                fieldWithPath("data").description("메시지 본문").type(JsonFieldType.OBJECT)
        ).andWithPrefix("data.", DocsUtils.mergeFields(field));
    }

    public static FieldDescriptors withPaginations(FieldDescriptors field){
        return new FieldDescriptors(
                fieldWithPath("data[]").description("메시지 본문").type(JsonFieldType.ARRAY),
                fieldWithPath("pagination.count").description("현재 페이지의 데이터 개수").type(JsonFieldType.NUMBER),
                fieldWithPath("pagination.requestNumber").description("요청한 페이지 번호").type(JsonFieldType.NUMBER),
                fieldWithPath("pagination.requestSize").description("요청한 데이터의 개수").type(JsonFieldType.NUMBER),
                fieldWithPath("pagination.hasNextPage").description("다음 페이지 존재 유무").type(JsonFieldType.BOOLEAN),
                fieldWithPath("pagination.totalPage").description("전체 페이지의 개수").type(JsonFieldType.NUMBER),
                fieldWithPath("pagination.totalElements").description("전체 데이터 개수").type(JsonFieldType.NUMBER)
                ).andWithPrefix("data[].", DocsUtils.mergeFields(field));
    }

}
