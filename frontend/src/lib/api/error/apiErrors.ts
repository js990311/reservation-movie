import {ExceptionResponse} from "@/src/type/response/exceptionResponse";

export class BaseError extends Error{
    readonly details: ExceptionResponse;

    constructor(details: ExceptionResponse) {
        super(`[${details.type} in (${details.instance})]} ${details.title} : ${details.detail}`);
        this.details = details;
    }

}

export class BusinessError extends BaseError {
}

export class FetchError extends BaseError {
}

export function unknownFetchException(instance: string, cause?: unknown): FetchError {
    return new FetchError({
        type: 'UNKNOWN_ERROR',
        title: 'Unknown Error',
        status: 500,
        instance,
        detail:
            cause instanceof Error
                ? cause.message
                : '알 수 없는 오류가 발생했습니다.',
    })
}

export function networkException(instance: string, cause?: unknown): FetchError {
    return new FetchError({
        type: 'NETWORK_ERROR',
        title: 'Network Error',
        status: 0,
        instance,
        detail:
            cause instanceof Error
                ? cause.message
                : '네트워크 연결에 실패했습니다.',
    })
}

export function invalidResponseException(instance: string): FetchError {
    return new FetchError({
        type: 'INVALID_RESPONSE',
        title: 'Invalid Response',
        status: 500,
        instance,
        detail: '서버 응답 형식이 올바르지 않습니다.',
    })
}

export function timeoutException(instance: string): FetchError {
    return new FetchError({
        type: 'TIMEOUT',
        title: 'Request Timeout',
        status: 408,
        instance,
        detail: '요청 시간이 초과되었습니다.',
    })
}

export function httpException(instance: string, status: number): FetchError {
    return new FetchError({
        type: 'HTTP_ERROR',
        title: 'HTTP Error',
        status: status,
        instance: instance,
        detail: `HTTP ${status}`,
    })
}

export function unexpectedException(instance: string, e:unknown): BaseError{
    return new BaseError({
            type: 'UNEXPECTED_ERROR',
            title: 'Unexpected Error',
            status: 500,
            instance: '/login',
            detail: e instanceof Error ? e.message : '알 수 없는 오류가 발생했습니다.',
    });
}