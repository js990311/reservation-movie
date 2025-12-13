"use server"

import {Movie} from "@/src/type/movie/movie";
import {BaseResponse} from "@/src/type/response/base";
import {serverFetch} from "@/src/lib/api/serverFetch";
import {createInternalServerException} from "@/src/type/error/ApiError";
import {ScreeningWithTheater} from "@/src/type/screening/screening";
import {fetchList, fetchOne} from "@/src/lib/api/fetchWrapper";
import {ActionListResult, ActionOneResult, failResult, listResult, oneResult} from "@/src/type/response/result";
import {BaseError, unknownFetchException} from "@/src/lib/api/error/apiErrors";

export async function getMovieByIdAction(id: string): Promise<ActionOneResult<Movie>>{
    try {
        const response = await fetchOne<Movie>({
            endpoint: `/movies/${id}`
        });
        return oneResult<Movie>(response);
    }catch (error) {
        if(error instanceof BaseError){
            return failResult(error.details);
        }else {
            const exception = unknownFetchException('getMovieByIdAction',error);
            return failResult(exception.details);
        }
    }
}

export async function getMoviesAction(page: number, size: number): Promise<ActionListResult<Movie>> {
    try {
        const response = await fetchList<Movie>({
            endpoint: `/movies?page=${page}&size=${size}`,
        });
        return listResult<Movie>(response);
    }catch (error) {
        if(error instanceof BaseError){
            return failResult(error.details);
        }else {
            const exception = unknownFetchException('getMoviesAction',error);
            return failResult(exception.details);
        }
    }
}

export async function getMovieScreeningAction(id: string, date:string): Promise<ActionListResult<ScreeningWithTheater>> {
    try {
        const response = await fetchList<ScreeningWithTheater>({
            endpoint: `/movies/${id}/screenings?date=${date}`,
        });
        return listResult<ScreeningWithTheater>(response);
    }catch (error) {
        if(error instanceof BaseError){
            return failResult(error.details);
        }else {
            const exception = unknownFetchException('getMovieScreeningAction',error);
            return failResult(exception.details);
        }
    }
}