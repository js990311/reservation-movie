"use server"

import {PaginationResponse} from "@/src/type/response/pagination";
import {Movie} from "@/src/type/movie/movie";
import {ProxyRequestBuilder} from "@/src/lib/api/proxyRequestBuilder";
import {BaseResponse} from "@/src/type/response/base";

export async function getMoviesAction(page: number, size: number): Promise<PaginationResponse<Movie>> {
    try {
        const response = await new ProxyRequestBuilder(`/movies?page=${page}&size=${size}`)
            .withMethod('GET')
            .execute()
        ;
        const respData: PaginationResponse<Movie> = await response.json();
        return respData;
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
            }
        };
    }


}