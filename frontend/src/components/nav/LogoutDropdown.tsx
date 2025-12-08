import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuItem,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import {Button} from "@/components/ui/button";
import LogoutButton from "@/src/components/nav/LogoutButton";
import Link from "next/link";

export default function LogoutDropdown() {
    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button>MyPage</Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
                <DropdownMenuGroup>
                    <DropdownMenuItem>
                        <Link href={"/reservations/me"}>
                            마이페이지
                        </Link>
                    </DropdownMenuItem>
                    <DropdownMenuItem>
                        <LogoutButton />
                    </DropdownMenuItem>
                </DropdownMenuGroup>
            </DropdownMenuContent>
        </DropdownMenu>
    )
}