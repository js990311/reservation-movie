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
import {Suspense, useEffect, useState} from "react";
import toast from "react-hot-toast";
import {useRouter, useSearchParams} from "next/navigation";
import {signIn} from "next-auth/react";

export default function LoginSuspendPage(){
    return (
        <Suspense>
            <LoginPage></LoginPage>
        </Suspense>
    )
}

function LoginPage(){
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const router = useRouter();
    const searchParams = useSearchParams();

    const onLogin = async () => {
        const result = await signIn("credentials", {
            username,
            password,
            redirect: false, // 함수 내부에서 직접 처리하기 위해 false 설정
        });

        if (result?.error) {
            // 에러 메시지 처리 (백엔드에서 던진 에러에 따라 대응 가능)
            toast.error("로그인에 실패했습니다. 아이디 또는 비밀번호를 확인하세요.");
        } else {
            const callbackUrl = searchParams.get("callbackUrl") ?? "/";
            toast.success("로그인 성공!");
            router.push(callbackUrl);
            router.refresh(); // 세션 상태를 모든 컴포넌트에 반영하기 위해 새로고침 권장
        }
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