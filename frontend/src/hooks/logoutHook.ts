import {useState} from "react";
import {clientException, ExceptionResponse} from "@/src/type/exception/exceptionResponse";
import useLoginStore from "@/src/hooks/loginStoreHook";
import {apiClient, apiClientResponse} from "@/src/lib/api/apiClient";
import toast from "react-hot-toast";
import {useRouter} from "next/navigation";

export const useLogout = () => {
    const [status, setStatus] = useState<RequestStatus>('PENDING');
    const [error, setError] = useState<ExceptionResponse | null>(null);
    const onLogout = useLoginStore(state => state.onLogout);
    const router = useRouter();

    const logout = async (): Promise<boolean> => {
        try {
            setStatus('LOADING');
            const response: apiClientResponse<{ok:boolean}> = await apiClient<{ok:boolean}>('/api/logout', {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                }
            });

            if(response.ok){
                toast.success("로그아웃 성공");
                setStatus('SUCCESS');
                onLogout();
                router.push("/");
            }else {
                setStatus('FAIL');
                setError(response.error);
                toast.error(`${error?.type} : ${error?.detail}`);
            }
        }catch (exception) {
            setStatus('FAIL');
            setError(clientException('/logout', exception));
            toast.error(`${error?.type} : ${error?.detail}`);
        }
    }

    return {
        status, error, logout
    };
}