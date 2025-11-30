export interface ExceptionResponse {
    type: string,
    title: string,
    status: number,
    instance: string,
    detail: string,
}

export function clientException(instance:string, error: unknown) : ExceptionResponse{
    return {
        type: 'NEXT_SIDE_EXCEPTION',
        title: 'NEXT 측에서 문제가 발생했습니다.',
        status: 500,
        detail: error instanceof Error ? error.message : '이유를 추적할 수 없습니다.',
        instance: instance
    }
}

