"use server"

import {Movie} from "@/src/type/movie/movie";
import {BaseResponse} from "@/src/type/response/base";
import {serverFetch} from "@/src/lib/api/serverFetch";
import {createInternalServerException} from "@/src/type/error/ApiError";
import {logger} from "@/src/lib/logger/logger";
import {ScreeningWithTheater} from "@/src/type/screening/screening";

export async function getMovieByIdAction(id: string){
    try {
        const response = await serverFetch<Movie>({
            endpoint: `/movies/${id}`
        });
        if(response.error){
            logger.apiError(response.error);
        }
        return response.data;
    }catch (error) {
        logger.apiError(createInternalServerException(`/getMovieByIdAction(id=${id})`, error));
        return null;
    }
}

export async function getMoviesAction(page: number, size: number): Promise<BaseResponse<Movie[]>> {
    try {
        return await serverFetch<Movie[]>({
            endpoint: `/movies?page=${page}&size=${size}`,
        });
    }catch (error) {
        return {
            data: [],
            pagination: {
                count: 0,
                requestNumber: page+1,
                requestSize: size,
                hasNextPage: false,
                totalPage: 0,
                totalElements: 0
            },
            error: createInternalServerException('/getMovieAction', error)
        };
    }
}

export async function getMovieScreeningAction(id: string, date:string) {
    try {
        const response = await serverFetch<ScreeningWithTheater[]>({
            endpoint: `/movies/${id}/screenings?date=${date}`,
        });
        return response.data;
    }catch (error) {
        return [];
    }
}