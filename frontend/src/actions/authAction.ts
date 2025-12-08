"use server"

import {LoginRequest} from "@/src/type/login/LoginRequest";
import {ExceptionResponse} from "@/src/type/response/exceptionResponse";
import {serverFetch} from "@/src/lib/api/serverFetch";
import {Tokens} from "@/src/type/token/tokens";
import {clearTokens, setTokens} from "@/src/lib/api/tokenUtil";
import {ApiError, createInternalServerException} from "@/src/type/error/ApiError";

type AuthActionResponse = {
    success: boolean,
    error?: ExceptionResponse
}

export async function loginAction(request: LoginRequest): Promise<AuthActionResponse> {
    try {
        const response = await serverFetch<Tokens>({
            endpoint: '/login',
            method: 'POST',
            body: request
        });
        if(response.data){
            await setTokens(response.data);
            return {success: true};
        }
        return {
            success: false,
            error: response.error
        }
    }catch (error) {
        if(error instanceof ApiError) {
            return {
                success: false,
                error: error.exception
            }
        }
        return {
            success: false,
            error: createInternalServerException('/login', error)
        };
    }
}

export async function signupAction(request: LoginRequest): Promise<AuthActionResponse> {
    try {
        const response = await serverFetch<Tokens>({
            endpoint: '/signup',
            method: 'POST',
            body: request
        });
        if(response.data){
            await setTokens(response.data);
            return {success: true};
        }
        return {
            success: false,
            error: response.error
        }
    }catch (error) {
        if(error instanceof ApiError) {
            return {
                success: false,
                error: error.exception
            }
        }
        return {
            success: false,
            error: createInternalServerException('/login', error)
        };
    }
}

export async function logoutAction(){
    await clearTokens();
}