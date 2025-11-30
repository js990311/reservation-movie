import {apiClient, apiClientResponse} from "@/src/lib/api/apiClient";
import {useState} from "react";
import {LoginRequest} from "@/src/type/login/LoginRequest";
import {ExceptionResponse} from "@/src/type/exception/exceptionResponse";
import useLoginStore from "@/src/hooks/loginStoreHook";

export const useSignup = () => {
    const [success, setSuccess] = useState<boolean>(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<ExceptionResponse | null>(null);
    const onLogin = useLoginStore(state => state.onLogin);

    const signup = async (request: LoginRequest) => {
        try {
            setLoading(true);
            const response: apiClientResponse<{ok:boolean}> = await apiClient<{ok:boolean}>('/api/signup', {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(request),
            });

            if(response.ok){
                setSuccess(true);
                onLogin();
            }else {
                setError(response.error);
            }
        }finally {
            setLoading(false);
        }
    }

    return {
        success, loading, error, signup
    };
}