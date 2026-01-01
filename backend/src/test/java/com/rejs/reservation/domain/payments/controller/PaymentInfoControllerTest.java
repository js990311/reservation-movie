package com.rejs.reservation.domain.payments.controller;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.github.f4b6a3.tsid.TsidCreator;
import com.rejs.reservation.controller.AbstractMockControllerTest;
import com.rejs.reservation.controller.docs.BaseResponseDocs;
import com.rejs.reservation.domain.payments.dto.PaymentCancelInfo;
import com.rejs.reservation.domain.payments.dto.PaymentInfo;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelStatus;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import com.rejs.reservation.domain.payments.service.PaymentInfoService;
import com.rejs.reservation.global.security.jwt.resolver.TokenClaim;
import com.rejs.reservation.global.security.jwt.resolver.TokenClaimArgumentResolver;
import com.rejs.reservation.global.security.jwt.token.ClaimsDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PaymentInfoController.class)
class PaymentInfoControllerTest extends AbstractMockControllerTest {
    @MockitoBean
    private PaymentInfoService paymentInfoService;

    @MockitoBean
    public TokenClaimArgumentResolver tokenClaimArgumentResolver;

    @Test
    @WithMockUser
    @DisplayName("나의 결제 내역 조회 - 성공")
    void getMyPayment_Success() throws Exception {
        String paymentUid = TsidCreator.getTsid().toString();
        PaymentStatus status = PaymentStatus.PAID;
        // given
        PaymentInfo paymentInfo = new PaymentInfo(
                paymentUid,
                status,
                100L,
                LocalDateTime.now(),
                new PaymentCancelInfo(PaymentCancelStatus.CANCELED, null, PaymentCancelReason.VALIDATION_FAILED.toString())
        );
        ClaimsDto mockClaims = ClaimsDto.builder()
                .username("1")
                .roles(List.of("ROLE_USER"))
                .build();
        when(tokenClaimArgumentResolver.supportsParameter(argThat(
                p -> p.getParameterType().equals(ClaimsDto.class)
        ))).thenReturn(true);
        when(tokenClaimArgumentResolver.resolveArgument(any(),any(),any(),any())).thenReturn(mockClaims);

        PageImpl<PaymentInfo> pageResponse = new PageImpl<>(List.of(paymentInfo), PageRequest.of(0, 10), 1);

        given(paymentInfoService.getMyPayment(anyLong(), any()))
                .willReturn(pageResponse); // 서비스 호출 시 가짜 페이지 반환 설정

        // when: API 호출
        ResultActions result = mockMvc.perform(
                get("/payments/me")
                        .header("Authorization", "Bearer ACCESS_TOKEN")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].paymentUid").value(paymentUid))
                .andExpect(jsonPath("$.data[0].paymentStatus").value(status.toString()))
        ;

        result.andDo(document(docs -> docs
                .summary("내 결제 내역 조회 API")
                .description("로그인한 사용자의 결제 내역을 페이징하여 조회합니다.")
                .queryParameters(
                        parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                        parameterWithName("size").description("한 페이지 당 데이터 개수")
                )
                .responseFields(BaseResponseDocs.withPaginations(paymentInfo()))
        ));
    }

    public FieldDescriptors paymentInfo(){
        return new FieldDescriptors(
                fieldWithPath("paymentUid").type(JsonFieldType.STRING).description("결제 식별자(portOne기준)"),
                fieldWithPath("paymentStatus").type(JsonFieldType.STRING).description("결제 상태"),
                fieldWithPath("reservationId").type(JsonFieldType.NUMBER).description("예매 ID"),
                fieldWithPath("createAt").type(JsonFieldType.STRING).description("결제 생성 시간"),
                fieldWithPath("cancelInfo").type(JsonFieldType.OBJECT).description("취소 정보").optional(),
                fieldWithPath("cancelInfo.cancelStatus").type(JsonFieldType.STRING).description("취소 상태").optional(),
                fieldWithPath("cancelInfo.canceledAt").type(JsonFieldType.STRING).description("취소 시간").optional(),
                fieldWithPath("cancelInfo.cancelReason").type(JsonFieldType.STRING).description("취소 사유").optional()
        );
    }
}