"use server"

import {
    Reservation,
    ReservationRequest,
    ReservationSummary
} from "@/src/type/reservation/reservation";
import {ProxyRequestBuilder} from "@/src/lib/api/proxyRequestBuilder";
import {BaseResponse} from "@/src/type/response/base";
import {ExceptionResponse} from "@/src/type/exception/exceptionResponse";
import {PaginationResponse} from "@/src/type/response/pagination";

export async function getMyReservationsAction(page: number, size: number): Promise<PaginationResponse<ReservationSummary>> {
    try {
        const response = await new ProxyRequestBuilder(`/reservations/me?page=${page}&size=${size}`)
            .withMethod('GET')
            .withAuth()
            .execute()
        ;
        const respData: PaginationResponse<ReservationSummary> = await response.json();
        return respData;
    }catch (error) {
        return {
            data: [],
            pagination: {
                count: 0,
                requestNumber: page,
                requestSize: size,
                hasNextPage: false,
                totalPage: 0,
                totalElements: 0
            }
        };
    }
}


export async function reservationAction(request: ReservationRequest){
    try {
        const response = await new ProxyRequestBuilder(`/reservations`)
            .withMethod('POST')
            .withBody(request)
            .withAuth()
            .execute()
        ;
        if(response.ok){
            console.log("reservation response ok");
            const reservation:BaseResponse<Reservation> = await response.json();
            return reservation.data;
        }else {
            const exception : ExceptionResponse = await response.json();
            console.log(`${exception.type} : ${exception.title} / ${exception.detail}`);
            return exception;
        }
    }catch (error) {
        return {
            type: 'NEXT_SERVER_ACTION_ERROR',
            title: 'NEXT 서버 액션측에서 문제가 발생했습니다.',
            status: 500,
            detail: error instanceof Error ? error.message : '이유를 추적할 수 없습니다.',
            instance: `reservationAction(${request.screeningId}, ${request.seats.length})`
        }
    }

}