import NextAuth, { DefaultSession } from "next-auth";

declare module "next-auth" {
    interface Session {
        accessToken?: string;
        accessTokenExpire?: number;
        user: {
            role?: string;
        } & DefaultSession["user"];
    }

    interface User {
        email: string;
        roles: string[];
        tokens: {
            accessToken: { token: string; expiresAt: number };
            refreshToken: { token: string; expiresAt: number };
        };
    }
}

declare module "next-auth/jwt" {
    interface JWT {
        role?: string;
        accessToken?: string;
        accessTokenExpire?: number;
    }
}