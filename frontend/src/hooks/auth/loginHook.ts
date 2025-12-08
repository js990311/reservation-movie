"use client"

import useLoginStore from "@/src/hooks/auth/loginStoreHook";
import {ExceptionResponse} from "@/src/type/response/exceptionResponse";
import {useState} from "react";
import {loginAction, signupAction} from "@/src/actions/authAction";
import {LoginRequest} from "@/src/type/login/LoginRequest";
import {logger} from "@/src/lib/logger/logger";
import {createInternalClientException, createInternalServerException} from "@/src/type/error/ApiError";
import {useRequestResult} from "@/src/hooks/status/clientStatus";

export const useLogin = () => {
    const {result, setLoading, setSuccess, setFail} = useRequestResult();
    const onLogin = useLoginStore(state => state.onLogin);

    const login = async (request: LoginRequest) => {
        setLoading();
        try {
            const result = await loginAction(request);
            if(result.success) {
                onLogin();
                setSuccess();
            }else {
                setFail(result.error ?? createInternalClientException('/login', 'unknown error'));
            }
        }catch (e) {
            const exceptionResponse = createInternalClientException('/login',e);
            logger.apiError(exceptionResponse);
            setFail(exceptionResponse);
        }
    }

    return {
        status: result.status, error: result.error, login
    };
}