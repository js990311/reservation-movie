export type PaginationMetadata = {
    count: number,
    requestNumber:  number,
    requestSize: number,
    hasNextPage: boolean,
    totalPage: number,
    totalElements: number
}

export interface PaginationResponse<T>{
    data: T[];
    pagination: PaginationMetadata;
}