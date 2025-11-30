"use client"
import {Button} from "@/components/ui/button";
import {useLogout} from "@/src/hooks/logoutHook";
import {useEffect} from "react";
import {router} from "next/client";
import toast from "react-hot-toast";

export default function LogoutButton() {
    const { status, error, logout } = useLogout();

    return (
        <Button onClick={() => logout()}>
            로그아웃
        </Button>
    );
}