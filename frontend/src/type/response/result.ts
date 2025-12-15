import {ExceptionResponse} from "@/src/type/response/exceptionResponse";
import {PaginationMetadata} from "@/src/type/response/pagination";
import {ApiListResponse, ApiOneResponse} from "@/src/type/response/apiResponse";
import {BaseError} from "@/src/lib/api/error/apiErrors";

export type ActionListResult<T> = {
    ok: true;
    data: T[];
    pagination: PaginationMetadata;
} | ActionFailResult;

export type ActionOneResult<T> = {
    ok: true;
    data: T;
} | ActionFailResult;


export type ActionFailResult = {
    ok: false;
    error: ExceptionResponse;
}

export function voidResult(): ActionOneResult<void>{
    return {
        ok: true,
        data: undefined
    };
}

export function oneResult<T>(one: ApiOneResponse<T>): ActionOneResult<T>{
    return {
        ok: true,
        data: one.data,
    }
}

export function listResult<T>(list: ApiListResponse<T>): ActionListResult<T> {
    return {
        ok: true,
        data: list.data,
        pagination: list.pagination,
    }
}

export function failResult(error: ExceptionResponse): ActionFailResult {
    return {
        ok:false,
        error: error
    };
}