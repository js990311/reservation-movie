"use server"

import {ScreeningWithMovie} from "@/src/type/screening/screening";
import {serverFetch} from "@/src/lib/api/serverFetch";
import {createInternalServerException} from "@/src/type/error/ApiError";
import {Theater} from "@/src/type/theater/theater";
import {logger} from "@/src/lib/logger/logger";

export async function getTheaterByIdAction(id: string) {
    try {
        const response = await serverFetch<Theater>({
            endpoint: `/theaters/${id}`
        });
        if(response.error) {
            console.log(response.error);
        }
        return response.data;
    }catch (error) {
        return null;
    }
}

export async function getTheatersAction(){
    try {
        const response = serverFetch<Theater[]>({
            endpoint: `/theaters`
        })
        return response;
    }catch (error) {
        return {
            data: [],
            pagination: {
                count: 0,
                requestNumber: 1,
                requestSize: 10,
                hasNextPage: false,
                totalPage: 0,
                totalElements: 0
            },
            error: createInternalServerException('/getMovieAction', error)
        };
    }
}


export default async function getTheaterScreeningAction(id: string, date:string) {
    try {
        const response = await serverFetch<ScreeningWithMovie[]>({
            endpoint: `/theaters/${id}/screenings?date=${date}`
        })
        return response.data;
    }catch (error) {
        logger.apiError(createInternalServerException(`/getTheaterScreeningAction(id=${id}, date=${date})`, error))
        return [];
    }
}
