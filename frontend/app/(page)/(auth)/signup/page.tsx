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
import {useSignup} from "@/src/hooks/auth/signupHook";
import {useEffect, useState} from "react";
import toast from "react-hot-toast";
import {useRouter} from "next/navigation";
import useLoginStore from "@/src/hooks/auth/loginStoreHook";
import {useLogin} from "@/src/hooks/auth/loginHook";

export default function SignupPage(){
    const {status, error, signup} = useSignup();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const router = useRouter();

    useEffect(() => {
        if( status === "SUCCESS"){
            toast.success('회원가입성공');
            router.push("/");
        }else if(status === "FAIL"){
            toast.error(`[${error?.type}] ${error?.title} : ${error?.detail}`);
        }
    }, [status]);

    const onSignup = async () => {
        await signup({
            username, password
        });
    }

    return (
        <div>
            <Card>
                <CardHeader>
                    <CardTitle>회원가입 페이지</CardTitle>
                    <CardDescription>회원가입하십시오</CardDescription>
                    <CardAction>signup</CardAction>
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
            <CardFooter>
                <Button
                    onClick={onSignup}
                    type={"submit"} className={"w-full"}>회원가입하기</Button>
            </CardFooter>
        </Card>
    </div>)
}