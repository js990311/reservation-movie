"use client"

import {
    Card,
    CardAction,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"
import {Label} from "@/components/ui/label";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import Link from "next/link";
import {useLogin} from "@/src/hooks/auth/loginHook";
import {useEffect, useState} from "react";
import toast from "react-hot-toast";
import {useRouter, useSearchParams} from "next/navigation";

export default function LoginPage(){
    const {status, login} = useLogin();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const router = useRouter();
    const searchParams = useSearchParams();

    useEffect(() => {
        if( status === "SUCCESS"){
            toast.success('로그인성공');
            const callbackUrl = searchParams.get("callbackUrl") || "/";
            router.push(callbackUrl);
        }
    }, [status]);

    const onLogin = async () => {
        await login({
            username, password
        });
    }

    return (
        <div>
            <Card>
                <CardHeader>
                    <CardTitle>로그인 페이지</CardTitle>
                    <CardDescription>login하십시오</CardDescription>
                    <CardAction>Login</CardAction>
                </CardHeader>
                <CardContent>
                    <div>
                        <Label htmlFor={"username"}>
                            아이디
                        </Label>
                        <Input
                            id="username"
                            type="email"
                            value={username}
                            placeholder="example@example.com"
                            required={true}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                    </div>
                    <div className="py-4">
                        <Label htmlFor={"password"}>
                            비밀번호
                        </Label>
                        <Input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>
                </CardContent>
                <CardFooter className={"block"}>
                    <div className={"w-full"}>
                        <Button
                            onClick={onLogin}
                            type={"submit"} className={"w-full"}>로그인하기</Button>
                    </div>
                    <div className={"pt-3 flex justify-center gap-2.5"}>
                        <Link href={"/signup"}>회원가입하기</Link>
                        <Link href={"/"}>비밀번호 찾기</Link>
                    </div>
                </CardFooter>
            </Card>
        </div>
    )
}