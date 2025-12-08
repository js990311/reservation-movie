"use client"
import {Button} from "@/components/ui/button";
import {useLogout} from "@/src/hooks/auth/logoutHook";

export default function LogoutButton() {
    const {logout } = useLogout();

    return (
        <Button onClick={() => logout()}>
            로그아웃
        </Button>
    );
}