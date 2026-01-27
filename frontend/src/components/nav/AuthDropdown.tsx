"use client"

import LoginDropdown from "@/src/components/nav/LoginDropdown";
import LogoutDropdown from "@/src/components/nav/LogoutDropdown";
import useLoginStore from "@/src/hooks/auth/loginStoreHook";
import {useEffect} from "react";
import {useSession} from "next-auth/react";

export default function AuthDropdown() {
    const { data: session, status } = useSession();
    return (
        <>
            {
                status === 'authenticated'
                    ? <LogoutDropdown />
                    : <LoginDropdown />
            }
        </>
    );
}