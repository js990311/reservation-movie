"use server"

import {failResult, voidResult} from "@/src/type/response/result";
import {fetchOne} from "@/src/lib/api/fetchWrapper";
import {BaseError, unknownFetchException} from "@/src/lib/api/error/apiErrors";
import {LoginResponse} from "@/src/type/token/tokens";
import {LoginRequest} from "@/src/type/login/LoginRequest";

export async function signUpAction(request: LoginRequest){
    try {
        await fetchOne<LoginResponse>({
            endpoint: "/signup",
            method: "POST",
            body: request,
            withAuth: false
        });
        console.log("Signup request successfully");
        return voidResult();
    }catch (error) {
        if(error instanceof BaseError){
            return failResult(error.details);
        }else {
            const exception = unknownFetchException('reservationAction',error);
            return failResult(exception.details);
        }
    }
}
