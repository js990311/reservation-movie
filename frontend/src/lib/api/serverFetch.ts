import {
    BaseResponse
} from "@/src/type/response/base";
import {getAccessToken} from "@/src/lib/api/tokenUtil";
import {ApiError, createInternalServerException} from "@/src/type/error/ApiError";
import {logger} from "@/src/lib/logger/logger";

const BACKEND_HOST = process.env.BACKEND_HOST ?? 'http://localhost:8080/api';

type ServerFetchProps = {
    endpoint: string;
    method?: "GET" | "POST" | "PUT" | "DELETE";
    headers?: Record<string, string>;
    body?: unknown;
    withAuth?:boolean;
}

export async function serverFetch<T>({endpoint, method='GET', headers, withAuth, body} : ServerFetchProps) : Promise<BaseResponse<T>> {
    const requestHeaders : Record<string, string>= {
        'Content-Type': 'application/json',
        ...headers,
    };

    if(withAuth){
        const accessToken = await getAccessToken();
        if(accessToken){
            requestHeaders['Authorization'] = `Bearer ${accessToken}`;
        }
    }

    const url = `${BACKEND_HOST}${endpoint}`;

    try {
        const response = await fetch(url, {
            method: method,
            headers: requestHeaders,
            body: body ? JSON.stringify(body) : null
        });

        const text = await response.text();
        const data: BaseResponse<T> = text ? JSON.parse(text) : undefined;
        if(!response.ok){
            logger.apiError(data.error);
        }
        return data;
    }catch (error){
        if(error instanceof ApiError){
            logger.apiError(error.exception);
            throw error;
        }
        const serverSideException = createInternalServerException(endpoint, error);
        logger.apiError(serverSideException);
        throw new ApiError(500, serverSideException);
    }
}

