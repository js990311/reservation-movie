import useLoginStore from "@/src/hooks/auth/loginStoreHook";
import toast from "react-hot-toast";
import {useRouter} from "next/navigation";
import {logoutAction} from "@/src/actions/authAction";
import {unexpectedException} from "@/src/lib/api/error/apiErrors";

export const useLogout = () => {
    const onLogout = useLoginStore(state => state.onLogout);
    const router = useRouter();

    const logout = async () => {
        try {
            const response = await logoutAction();
            if(response.success){
                onLogout();
                toast.success("로그아웃 완료");
                router.push("/login");
                router.refresh();
            }
        }catch (error) {
            const exceptionResponse = unexpectedException('useLogout',error);
            toast.error(exceptionResponse.details.detail);
        }
    }

    return {
        logout
    };
}