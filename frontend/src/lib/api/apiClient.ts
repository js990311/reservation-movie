import {clientException, ExceptionResponse} from "@/src/type/exception/exceptionResponse";

export interface apiClientResponse<T> {
    ok: boolean;
    error: ExceptionResponse | null;
    data: T | null;
}

export async function apiClient<T>(endpoint:string, options?:RequestInit) : Promise<apiClientResponse<T>> {
    try{
        const response = await fetch(endpoint, options as RequestInit ? options : {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            }
        });
        if(response.ok){
            if(response.status === 201 || response.status === 204){
                return {
                    data: null, error: null, ok: true
                }
            }
            const data: T = await response.json();
            return {
                data, error: null, ok: true
            }
        }else{
            const error: ExceptionResponse = await response.json();
            return{
                data: null, error: error, ok: false
            }
        }
    }catch (e){
        const error = clientException(endpoint, e);
        return{
            data: null, error: error, ok: false
        }
    }
}