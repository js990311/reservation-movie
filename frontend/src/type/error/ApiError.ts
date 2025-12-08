import {ExceptionResponse} from "@/src/type/response/exceptionResponse";
import {logger} from "@/src/lib/logger/logger";

export class ApiError extends Error {
    public status: number;
    public exception: ExceptionResponse;

    constructor(status: number, exception : ExceptionResponse) {
        super(exception.detail || exception.title);
        this.name = 'ApiError';
        this.status = status;
        this.exception = exception;
    }
}

export function getDetail(error: unknown){
    if(error instanceof Error){
        return error.message;
    }else if (typeof error === 'string'){
        return error;
    }else {
        return "Unknown Error";
    }
}

export function createInternalServerException(endpoint: string, error: unknown) :ExceptionResponse {
    return {
        type: "NEXT_SERVER_SIDE_ERROR",
        title: "NEXT_SERVER_SIDE_ERROR",
        status: 500,
        instance: endpoint,
        detail: getDetail(error)
    }
}

export function createInternalClientException(endpoint: string, error: unknown) :ExceptionResponse {
    return {
        type: "NEXT_CLIENT_SIDE_ERROR",
        title: "NEXT_CLIENT_SIDE_ERROR",
        status: 500,
        instance: endpoint,
        detail: getDetail(error)
    }
}