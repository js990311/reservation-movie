"use client"

import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"
import {Label} from "@/components/ui/label";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {useState} from "react";
import toast from "react-hot-toast";
import {useRouter} from "next/navigation";
import { signIn } from "next-auth/react";
import {fetchOne} from "@/src/lib/api/fetchWrapper";
import {LoginResponse} from "@/src/type/token/tokens";
import {signUpAction} from "@/src/actions/signUpAction";

export default function SignupPage(){
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const router = useRouter();

    const onSignup = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            await signUpAction({username, password});
            toast.success('회원가입 성공. 자동로그인 전환');

            // 자동로그인
            const result = await signIn("credentials", {
                username,
                password,
                redirect: false,
            });

            if (result?.ok) {
                router.push("/");
                router.refresh();
            } else {
                toast.error("자동 로그인에 실패했습니다. 로그인 페이지로 이동합니다.");
                router.push("/login");
            }
        } catch (error) {
            toast.error("회원가입에 실패했습니다.");
        } finally {
            setIsLoading(false);
        }
    }

    return (
        <form onSubmit={onSignup}>
            <Card>
                <CardHeader>
                    <CardTitle>회원가입 페이지</CardTitle>
                </CardHeader>
                <CardContent>
                    <div>
                        <Label htmlFor="username">아이디 (이메일)</Label>
                        <Input
                            id="username"
                            type="email"
                            value={username}
                            placeholder="example@example.com"
                            required
                            onChange={(e) => setUsername(e.target.value)}
                        />
                    </div>
                    <div className="py-4">
                        <Label htmlFor="password">비밀번호</Label>
                        <Input
                            id="password"
                            type="password"
                            value={password}
                            required
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>
                </CardContent>
                <CardFooter>
                    <Button
                        disabled={isLoading}
                        type="submit"
                        className="w-full"
                    >
                        {isLoading ? "가입 처리 중..." : "회원가입하기"}
                    </Button>
                </CardFooter>
            </Card>
        </form>
    )
}