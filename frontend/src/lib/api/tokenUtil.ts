import {cookies} from "next/headers";

export const ACCESS_TOKEN_KEY: string = 'access_token';
export const REFRESH_TOKEN_KEY: string = 'refresh_token';
export const LOGIN_STATUS: string = 'login_status';

import {Tokens} from "@/src/type/token/tokens";

export async function getAccessToken() : Promise<string | null>{
    const cookieStore = await cookies();
    const accessToken = cookieStore.get(ACCESS_TOKEN_KEY);
    return accessToken?.value ?? null;
}

export async function getRefreshToken(): Promise<string | null>{
    const cookieStore = await cookies();
    const refreshToken = cookieStore.get(REFRESH_TOKEN_KEY);
    return refreshToken?.value ?? null;
}

export async function setTokens(tokens: Tokens) {
    const cookieStore = await cookies();
    cookieStore.set(ACCESS_TOKEN_KEY, tokens.accessToken, {
        httpOnly: true
    });

    cookieStore.set(REFRESH_TOKEN_KEY, tokens.refreshToken, {
        httpOnly: true
    });

    cookieStore.set(LOGIN_STATUS, 'login', {
        httpOnly: false,
    });
}

export async function clearTokens(): Promise<void> {
    const cookieStore = await cookies();
    cookieStore.set(ACCESS_TOKEN_KEY, '', {
        httpOnly: true,
        maxAge: 0
    });

    cookieStore.set(REFRESH_TOKEN_KEY, '', {
        httpOnly: true,
        maxAge: 0
    });

    cookieStore.set(LOGIN_STATUS, 'logout', {
        httpOnly: false,
        maxAge: 0
    });

}