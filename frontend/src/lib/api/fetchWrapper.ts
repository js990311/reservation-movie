import {
    ApiFailResponse,
    ApiListResponse,
    ApiOneResponse,
} from "@/src/type/response/apiResponse";
import {setHeader} from "@/src/lib/api/fetchUtils";
import {BusinessError, FetchError, httpException, networkException} from "@/src/lib/api/error/apiErrors";

const BACKEND_HOST = process.env.BACKEND_HOST ?? 'http://localhost:8080/api';

type FetchParams = {
    endpoint: string;
    method?: "GET" | "POST" | "PUT" | "DELETE";
    headers?: Record<string, string>;
    body?: unknown;
    withAuth?:boolean;
}


export async function fetchOne<T>({endpoint, method='GET', headers, withAuth, body} : FetchParams) : Promise<ApiOneResponse<T>>{
    const respones: ApiOneResponse<T> = await fetchWrapper<ApiOneResponse<T>>({endpoint, method, headers, withAuth, body})
    return respones;
}

export async function fetchList<T>({endpoint, method='GET', headers, withAuth, body} : FetchParams) : Promise<ApiListResponse<T>>{
    const respones: ApiListResponse<T> = await fetchWrapper<ApiListResponse<T>>({endpoint, method, headers, withAuth, body})
    return respones;
}

export async function fetchVoid({endpoint, method='GET', headers, withAuth, body} : FetchParams) : Promise<ApiOneResponse<void>> {
    const respones: ApiOneResponse<void> = await fetchWrapper<ApiOneResponse<void>>({endpoint, method, headers, withAuth, body})
    return respones;
}

async function fetchWrapper<T>({endpoint, method='GET', headers, withAuth, body} : FetchParams): Promise<T>{
    try {
        const requestHeaders = await setHeader({headers, withAuth});
        const url = `${BACKEND_HOST}${endpoint}`;

        const response = await fetch(url, {
            method: method,
            headers: requestHeaders,
            body: body ? JSON.stringify(body) : null
        });

        if(!response.ok){
            const exceptions:ApiFailResponse = await response.json();
            if(exceptions){
                throw new BusinessError(exceptions.error);
            }
            throw httpException(endpoint, response.status);
        }

        if(response.status === 204){
            return undefined as T;
        }

        const data: T = await response.json();
        return data;
    }catch (error){
        if(error instanceof BusinessError){
            throw error;
        }
        if(error instanceof FetchError){
            throw error;
        }
        throw networkException(endpoint, error);
    }
}