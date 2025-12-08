import {BaseResponse} from "@/src/type/response/base";
import {ApiError, createInternalClientException} from "@/src/type/error/ApiError";
import {logger} from "@/src/lib/logger/logger";

type ClientFetchProps = {
    endpoint: string;
    method?: "GET" | "POST" | "PUT" | "DELETE";
    headers?: Record<string, string>;
    body?: unknown;
}


export async function fetchClient<T>({endpoint, method='GET', headers, body} : ClientFetchProps) : Promise<BaseResponse<T>> {
    const requestHeaders : Record<string, string>= {
        'Content-Type': 'application/json',
        ...headers,
    };

    try{
        const response = await fetch(endpoint, {
            method: method,
            headers: requestHeaders,
            body: body ? JSON.stringify(body) : undefined
        });

        const text = await response.text();
        const data: BaseResponse<T> = text ? JSON.parse(text) : null;

        if(!response.ok){
            const error = data?.error
                || createInternalClientException(
                    endpoint,
                    `Unknown ${response.status} ${response.statusText}`
                );
            throw new ApiError(response.status, error);
        }
        return data;
    }catch (error){
        if(error instanceof ApiError){
            logger.apiError(error.exception);
            throw error;
        }
        const clientError = createInternalClientException(endpoint, error);
        logger.apiError(clientError);
        throw new ApiError(500, clientError);
    }
}