"use server"

import {ProxyRequestBuilder} from "@/src/lib/api/proxyRequestBuilder";
import {PaginationResponse} from "@/src/type/response/pagination";
import {ScreeningWithMovie} from "@/src/type/screening/screening";


export default async function getTheaterScreeningAction(id: string, date:string) {
    try {
        const response = await new ProxyRequestBuilder(`/theaters/${id}/screenings?date=${date}`).withMethod("GET").execute();
        if(!response.ok) {
            return [];
        }
        const respData: PaginationResponse<ScreeningWithMovie> = await response.json();
        return respData.data;
    }catch (error) {
        return [];
    }
}
