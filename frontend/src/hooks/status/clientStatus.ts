import {ExceptionResponse} from "@/src/type/response/exceptionResponse";
import {useState} from "react";

export type RequestStatus = 'PENDING' | 'LOADING' | 'SUCCESS' | 'FAIL';

export interface RequestResult {
    status: RequestStatus;
    error ?: ExceptionResponse;
}

export const useRequestResult = () => {
    const [result, setResult] = useState<RequestResult>({status: 'PENDING'});
    const setLoading = () => {
        setResult({
            status: 'LOADING',
            error: undefined,
        });
    }

    const setSuccess = () => {
        setResult({
            status: 'SUCCESS',
            error: undefined,
        });
    }

    const setFail = (error: ExceptionResponse) => {
        setResult({
            status: 'FAIL',
            error: error,
        });
    }

    return {result, setLoading, setSuccess, setFail};
}