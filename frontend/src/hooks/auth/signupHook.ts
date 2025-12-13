import {LoginRequest} from "@/src/type/login/LoginRequest";
import useLoginStore from "@/src/hooks/auth/loginStoreHook";
import {signupAction} from "@/src/actions/authAction";
import {useRequestResult} from "@/src/hooks/status/clientStatus";
import {unexpectedException} from "@/src/lib/api/error/apiErrors";

export const useSignup = () => {
    const {result, setLoading, setSuccess, setFail} = useRequestResult();
    const onLogin = useLoginStore(state => state.onLogin);

    const signup = async (request: LoginRequest) => {
        setLoading();
        try {
            const result = await signupAction(request);
            if(result.success) {
                onLogin();
                setSuccess();
            }else {
                setFail(result.error);
            }
        }catch (e) {
            const exceptionResponse = unexpectedException('useSignup',e);
            setFail(exceptionResponse.details);
        }
    }

    return {
        status: result.status, error: result.error, signup
    };
}