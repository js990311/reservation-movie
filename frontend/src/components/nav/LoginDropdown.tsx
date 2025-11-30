import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuItem,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import {Button} from "@/components/ui/button";
import {NavigationMenuLink} from "@/components/ui/navigation-menu";
import Link from "next/link";

export default function LoginDropdown() {
    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button>Login</Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
                <DropdownMenuGroup>
                    <DropdownMenuItem>
                        <NavigationMenuLink asChild>
                            <Link href={"/login"}>로그인</Link>
                        </NavigationMenuLink>
                    </DropdownMenuItem>
                    <DropdownMenuItem>
                        <NavigationMenuLink asChild>
                            <Link href={"/signup"}>회원가입</Link>
                        </NavigationMenuLink>
                    </DropdownMenuItem>
                </DropdownMenuGroup>
            </DropdownMenuContent>
        </DropdownMenu>
    )
}