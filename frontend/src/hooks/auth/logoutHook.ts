import {useState} from "react";
import {clientException, ExceptionResponse} from "@/src/type/response/exceptionResponse";
import useLoginStore from "@/src/hooks/auth/loginStoreHook";
import toast from "react-hot-toast";
import {useRouter} from "next/navigation";
import {logoutAction} from "@/src/actions/authAction";

export const useLogout = () => {
    const onLogout = useLoginStore(state => state.onLogout);
    const router = useRouter();

    const logout = async () => {
        try {
            await logoutAction();
            onLogout();
            toast.success("로그아웃 완료");
            router.push("/login");
            router.refresh();
        }catch (error) {
            toast.error("로그아웃 실패");
            const exception = {
                type: 'LOGOUT_FAILED',
                title: '로그아웃실패',
                status: 500,
                instance: 'useLogout',
                detail: error instanceof Error ? error.message : error,
            };
            toast.error(`[${exception.type}] ${exception.title} : ${exception.detail}`);
        }
    }

    return {
        logout
    };
}