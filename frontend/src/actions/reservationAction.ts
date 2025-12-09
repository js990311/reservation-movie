"use server"

import {
    Reservation, ReservationDetail,
    ReservationRequest,
    ReservationSummary
} from "@/src/type/reservation/reservation";
import {BaseResponse} from "@/src/type/response/base";
import {serverFetch} from "@/src/lib/api/serverFetch";
import {createInternalServerException} from "@/src/type/error/ApiError";
import {logger} from "@/src/lib/logger/logger";

export async function getReservationIdAction(id: string){
    try {
        const response = await serverFetch<ReservationDetail>({
            endpoint: `/reservations/${id}`,
            withAuth: true
        });
        if(response.error){
            logger.apiError(response.error);
        }
        return response.data;
    }catch (error){
        logger.apiError(createInternalServerException(`getReservationIdAction(id=${id})`, error));
        return null;
    }
}


export async function getMyReservationsAction(page: number, size: number): Promise<BaseResponse<ReservationSummary[]>> {
    try {
        return await serverFetch<ReservationSummary[]>({
            endpoint:  `/reservations/me?page=${page}&size=${size}`,
            withAuth:true
        });
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
            },
            error: createInternalServerException('/getMyReservationsAction', error)
        };
    }
}

export async function reservationAction(request: ReservationRequest){
    try {
        return await serverFetch<Reservation>({
            endpoint:  `/reservations`,
            method: "POST",
            withAuth:true,
            body: request
        });
    }catch (error) {
        return {
            error: createInternalServerException('reservationAction(${request.screeningId}, ${request.seats.length})', error)
        };
    }
}

export async function reservationCancelAction(reservationId: number){
    try {
        const response = await serverFetch<null>({
            endpoint:  `/reservations/${reservationId}`,
            method: "DELETE",
            withAuth:true
        });
        if(response){
            if(response.error){
                return {
                    ok: false,
                    error: response.error
                };
            }else {
                return {
                    ok: false,
                    error: null
                }
            }
        }else {
            return {
                ok: true,
                error: null
            }
        }
    }catch (error) {
        return {
            ok: false,
            error: createInternalServerException('reservationAction(${request.screeningId}, ${request.seats.length})', error)
        };
    }
}