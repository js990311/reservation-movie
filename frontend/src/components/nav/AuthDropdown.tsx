"use client"
import {getAccessToken} from "@/src/lib/api/tokenUtil";
import LoginDropdown from "@/src/components/nav/LoginDropdown";
import LogoutDropdown from "@/src/components/nav/LogoutDropdown";
import useLoginStore from "@/src/hooks/loginStoreHook";

export default function AuthDropdown() {
    const isLogin = useLoginStore((state) => state.isLogin);

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