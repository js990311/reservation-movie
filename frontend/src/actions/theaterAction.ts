"use server"

import {ScreeningWithMovie} from "@/src/type/screening/screening";
import {Theater} from "@/src/type/theater/theater";
import {BaseError, unknownFetchException} from "@/src/lib/api/error/apiErrors";
import {failResult, listResult, oneResult} from "@/src/type/response/result";
import {fetchList, fetchOne} from "@/src/lib/api/fetchWrapper";

export async function getTheaterByIdAction(id: string) {
    try {
        const response = await fetchOne<Theater>({
            endpoint: `/theaters/${id}`
        });
        return oneResult(response);
    }catch (error) {
        if(error instanceof BaseError){
            return failResult(error.details);
        }else {
            const exception = unknownFetchException('getTheaterByIdAction',error);
            return failResult(exception.details);
        }
    }
}

export async function getTheatersAction(){
    try {
        const response = await fetchList<Theater>({
            endpoint: `/theaters`
        })
        return listResult<Theater>(response);
    }catch (error) {
        if(error instanceof BaseError){
            return failResult(error.details);
        }else {
            const exception = unknownFetchException('getTheatersAction',error);
            return failResult(exception.details);
        }
    }
}


export default async function getTheaterScreeningAction(id: string, date:string) {
    try {
        const response = await fetchList<ScreeningWithMovie>({
            endpoint: `/theaters/${id}/screenings?date=${date}`
        })
        return listResult(response);
    }catch (error) {
        if(error instanceof BaseError){
            return failResult(error.details);
        }else {
            const exception = unknownFetchException('getTheaterScreeningAction',error);
            return failResult(exception.details);
        }
    }
}
