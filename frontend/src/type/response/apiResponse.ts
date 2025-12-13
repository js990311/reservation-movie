import {PaginationMetadata} from "@/src/type/response/pagination";
import {ExceptionResponse} from "@/src/type/response/exceptionResponse";

export type ApiSuccessResponse<T> = ApiOneResponse<T>|  ApiListResponse<T>;

export type ApiOneResponse<T> = {
    data: T;
}

export type ApiListResponse<T> = {
    data: T[];
    pagination: PaginationMetadata;
}

export type ApiFailResponse = {
    error: ExceptionResponse;
}
