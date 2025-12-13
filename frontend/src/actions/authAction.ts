"use server"

import {LoginRequest} from "@/src/type/login/LoginRequest";
import {ExceptionResponse} from "@/src/type/response/exceptionResponse";
import {serverFetch} from "@/src/lib/api/serverFetch";
import {Tokens} from "@/src/type/token/tokens";
import {clearTokens, setTokens} from "@/src/lib/api/tokenUtil";
import {ApiError, createInternalServerException} from "@/src/type/error/ApiError";
import {fetchOne} from "@/src/lib/api/fetchWrapper";
import {BaseError, unexpectedException} from "@/src/lib/api/error/apiErrors";

type AuthActionResponse = {
    success: true,
} | {
    success: false;
    error: ExceptionResponse
}

export async function loginAction(request: LoginRequest): Promise<AuthActionResponse> {
    try {
        const response = await fetchOne<Tokens>({
            endpoint: '/login',
            method: 'POST',
            body: request
        });
        await setTokens(response.data);
        return {success: true};
    }catch (error) {
        if(error instanceof BaseError){
            return {success: false, error: error.details};
        }
        const exception = unexpectedException("/login", error);
        return {
            success: false,
            error: exception.details
        };
    }
}

export async function signupAction(request: LoginRequest): Promise<AuthActionResponse> {
    try {
        const response = await fetchOne<Tokens>({
            endpoint: '/signup',
            method: 'POST',
            body: request
        });
        await setTokens(response.data);
        return {success: true};
    }catch (error) {
        if(error instanceof BaseError){
            return {success: false, error: error.details};
        }
        const exception = unexpectedException("/login", error);
        return {
            success: false,
            error: exception.details
        };
    }
}

export async function logoutAction(): Promise<AuthActionResponse>{
    try {
        await clearTokens();
        return {success: true};
    }catch (error) {
        const exception = unexpectedException("/login", error);
        return {
            success: false,
            error: exception.details
        };
    }
}