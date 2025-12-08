"use client"

import LoginDropdown from "@/src/components/nav/LoginDropdown";
import LogoutDropdown from "@/src/components/nav/LogoutDropdown";
import useLoginStore from "@/src/hooks/auth/loginStoreHook";
import {useEffect} from "react";

type Props = {
    isLogin : boolean;
}

export default function AuthDropdown({isLogin}: Readonly<Props>) {
    const {onLogin, onLogout} = useLoginStore((state) => state);
    useEffect(() => {
        if(isLogin){
            onLogout();
        }else {
            onLogin();
        }
    }, [isLogin, onLogin, onLogout]);

    return (
        <>
            {
                isLogin
                    ? <LogoutDropdown />
                    : <LoginDropdown />
            }
        </>
    );
}