"use client"

import useLoginStore from "@/src/hooks/auth/loginStoreHook";
import {loginAction} from "@/src/actions/authAction";
import {LoginRequest} from "@/src/type/login/LoginRequest";
import {useRequestResult} from "@/src/hooks/status/clientStatus";
import {unexpectedException} from "@/src/lib/api/error/apiErrors";

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
                setFail(result.error);
            }
        }catch (e) {
            const exceptionResponse = unexpectedException('/login',e);
            setFail(exceptionResponse.details);
        }
    }

    return {
        status: result.status, error: result.error, login
    };
}