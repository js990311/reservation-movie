export interface TokenWithExpire {
    token: string;
    expiresAt: number; // Unix Timestamp (ms)
}

export interface Tokens {
    accessToken: TokenWithExpire;
    refreshToken: TokenWithExpire;
}

export interface LoginResponse {
    email: string;
    roles: string[];
    tokens: Tokens;
}