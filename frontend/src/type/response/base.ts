import {ExceptionResponse} from "@/src/type/response/exceptionResponse";
import {PaginationMetadata} from "@/src/type/response/pagination";

export interface BaseResponse<T>{
    data?: T;
    error ?: ExceptionResponse;
    pagination?: PaginationMetadata;
}

