import NextAuth, {NextAuthOptions} from 'next-auth';
import CredentialsProvider from 'next-auth/providers/credentials'
import {ApiOneResponse} from "@/src/type/response/apiResponse";
import {LoginResponse} from "@/src/type/token/tokens";

const BACKEND_HOST = process.env.BACKEND_HOST ?? 'http://localhost:8080/api';

export const authOptions: NextAuthOptions = {
    providers:[
        CredentialsProvider({
            name: 'Credentials',
            credentials:{
                username: {label: "username",type:'text'},
                password: {label: "password", type:'password'},
            },
            async authorize(credentials, req) {
                console.log(credentials);
                const response = await fetch(`${BACKEND_HOST}/login`,{
                    method:'POST',
                    body:JSON.stringify({
                        username: credentials.username,
                        password: credentials.password
                    }),
                    headers:{"Content-Type":"application/json"}
                });

                const loginResponse:ApiOneResponse<LoginResponse> = await response.json();
                if(response.ok && loginResponse.data){
                    return loginResponse.data;
                }
                return null;
            },
        })
    ],
    callbacks: {
        async jwt({token, user}){
            console.log(token);
            console.log(user);
            // next-auth 내부의 정보
            if(user){
                token.name = user.email;
                token.sub = user.email;
                token.email = user.email;
                token.role = user.role;
                token.accessToken= user.tokens.accessToken.token;
                token.refreshToken= user.tokens.refreshToken.token;
                token.accessTokenExpire = user.tokens.accessToken.expiresAt;
                token.refreshTokenExpire = user.tokens.refreshToken.expiresAt;
            }
            if(Date.now() < (token.accessTokenExpire as number)){
                return token;
            }else {
                console.log("REFRESH");
                const response = await fetch(`${BACKEND_HOST}/refresh`,{
                    method:'POST',
                    body:JSON.stringify({
                        refreshToken:token.refreshToken,
                    }),
                    headers:{"Content-Type":"application/json"}
                });
                const loginResponse:ApiOneResponse<LoginResponse> = await response.json();
                if(loginResponse){
                    return {
                        ...token,
                        accessToken: loginResponse.data.tokens.accessToken.token,
                        refreshToken: loginResponse.data.tokens.refreshToken.token,
                        accessTokenExpire: loginResponse.data.tokens.accessToken.expiresAt,
                        refreshTokenExpire: loginResponse.data.tokens.refreshToken.expiresAt
                    }
                }
            }
            return token;
        },
        async session({session, token}){
            // 브라우저에서 접근가능한 객체
            session.user = {
                email: token.email,
                name: token.name,
                role: token.role,
            };
            session.accessToken = token.accessToken;
            session.accessTokenExpire = token.accessTokenExpire;
            return session;
        }
    },
    pages:{
        signIn: '/login',
    }
}

const handler = NextAuth(authOptions);
export {handler as GET, handler as POST};