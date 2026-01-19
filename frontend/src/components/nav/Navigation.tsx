"use server"

import {
    NavigationMenu,
    NavigationMenuItem,
    NavigationMenuLink,
    NavigationMenuList
} from "@/components/ui/navigation-menu";
import Link from "next/link";
import AuthDropdown from "@/src/components/nav/AuthDropdown";

export default async function Navigation() {
    return (
        <NavigationMenu className={"flex-1 container mx-auto px-1 py-2 max-w-md md:max-w-2xl lg:max-w-4xl"}>
            <div className={"w-full"}>
                <NavigationMenuList className={"flex-wrap justify-between w-full"}>
                    <div className={"flex"}>
                        <NavigationMenuItem>
                            <NavigationMenuLink asChild>
                                <Link href={"/"}>HOME</Link>
                            </NavigationMenuLink>
                        </NavigationMenuItem>
                        <NavigationMenuItem>
                            <NavigationMenuLink asChild>
                                <Link href={"/movies"}>MOVIE</Link>
                            </NavigationMenuLink>
                        </NavigationMenuItem>
                        <NavigationMenuItem>
                            <NavigationMenuLink asChild>
                                <Link href={"/theaters"}>상영관</Link>
                            </NavigationMenuLink>
                        </NavigationMenuItem>
                    </div>
                    <div>
                        <NavigationMenuItem>
                            <AuthDropdown/>
                        </NavigationMenuItem>
                    </div>

                </NavigationMenuList>

            </div>
        </NavigationMenu>
    );
}