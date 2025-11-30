import {
    NavigationMenu, NavigationMenuContent,
    NavigationMenuItem,
    NavigationMenuLink,
    NavigationMenuList, NavigationMenuTrigger
} from "@/components/ui/navigation-menu";
import Link from "next/link";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuItem,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import {Button} from "@/components/ui/button";
import AuthDropdown from "@/src/components/nav/AuthDropdown";

export default function Navigation() {
    return (
        <NavigationMenu className={"flex-1 container mx-auto px-1 py-2 max-w-md md:max-w-2xl lg:max-w-4xl"}>
            <div className={"w-full"}>
                <NavigationMenuList className={"flex-wrap justify-between w-full"}>
                    <div>
                        <NavigationMenuItem>
                            <NavigationMenuLink asChild>
                                <Link href={"/"}>HOME</Link>
                            </NavigationMenuLink>
                        </NavigationMenuItem>
                    </div>
                    <div>
                        <NavigationMenuItem>
                            <AuthDropdown></AuthDropdown>
                        </NavigationMenuItem>
                    </div>

                </NavigationMenuList>

            </div>
        </NavigationMenu>
    );
}