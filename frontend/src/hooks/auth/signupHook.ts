import {LoginRequest} from "@/src/type/login/LoginRequest";
import useLoginStore from "@/src/hooks/auth/loginStoreHook";
import {loginAction, signupAction} from "@/src/actions/authAction";
import {createInternalClientException} from "@/src/type/error/ApiError";
import {logger} from "@/src/lib/logger/logger";
import {useRequestResult} from "@/src/hooks/status/clientStatus";

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
                setFail(result.error ?? createInternalClientException('/signup', 'unknown error'));
            }
        }catch (e) {
            const exceptionResponse = createInternalClientException('/signup',e);
            logger.apiError(exceptionResponse);
            setFail(exceptionResponse);
        }
    }

    return {
        status: result.status, error: result.error, signup
    };
}