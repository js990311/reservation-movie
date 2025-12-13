"use server"

import {
    Reservation, ReservationDetail,
    ReservationRequest,
    ReservationSummary
} from "@/src/type/reservation/reservation";
import {serverFetch} from "@/src/lib/api/serverFetch";
import {fetchList, fetchOne, fetchVoid} from "@/src/lib/api/fetchWrapper";
import {
    ActionListResult,
    ActionOneResult,
    failResult,
    listResult,
    oneResult,
    voidResult
} from "@/src/type/response/result";
import {BaseError, unknownFetchException} from "@/src/lib/api/error/apiErrors";

export async function getReservationIdAction(id: string): Promise<ActionOneResult<ReservationDetail>>{
    try {
        const response = await fetchOne<ReservationDetail>({
            endpoint: `/reservations/${id}`,
            withAuth: true
        });
        return oneResult(response);
    }catch (error){
        if(error instanceof BaseError){
            return failResult(error.details);
        }else {
            const exception = unknownFetchException('getReservationIdAction',error);
            return failResult(exception.details);
        }
    }
}


export async function getMyReservationsAction(page: number, size: number): Promise<ActionListResult<ReservationSummary>> {
    try {
        const response = await fetchList<ReservationSummary>({
            endpoint:  `/reservations/me?page=${page}&size=${size}`,
            withAuth:true
        });
        return listResult(response);
    }catch (error) {
        if(error instanceof BaseError){
            return failResult(error.details);
        }else {
            const exception = unknownFetchException('getMyReservationsAction',error);
            return failResult(exception.details);
        }
    }
}

export async function reservationAction(request: ReservationRequest): Promise<ActionOneResult<Reservation>>{
    try {
        const response = await fetchOne<Reservation>({
            endpoint:  `/reservations`,
            method: "POST",
            withAuth:true,
            body: request
        });
        return oneResult(response);
    }catch (error) {
        if(error instanceof BaseError){
            return failResult(error.details);
        }else {
            const exception = unknownFetchException('reservationAction',error);
            return failResult(exception.details);
        }
    }
}

export async function reservationCancelAction(reservationId: number){
    try {
        const response = await fetchVoid({
            endpoint:  `/reservations/${reservationId}`,
            method: "DELETE",
            withAuth:true
        });
        return voidResult();
    }catch (error) {
        if(error instanceof BaseError){
            return failResult(error.details);
        }else {
            const exception = unknownFetchException('reservationCancelAction',error);
            return failResult(exception.details);
        }
    }
}