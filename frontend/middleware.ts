import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';
import {ACCESS_TOKEN_KEY} from "@/src/lib/api/tokenUtil";

export function middleware(request: NextRequest) {
    const token = request.cookies.get(ACCESS_TOKEN_KEY);
    const { pathname } = request.nextUrl;

    const isProtectedPage = pathname.startsWith('/reservations') ||
        pathname.startsWith('/screenings') ||
        pathname.startsWith('/mypage');

    if (isProtectedPage && !token) {
        const url = new URL('/login', request.url);
        url.searchParams.set('callbackUrl', pathname);
        return NextResponse.redirect(url);
    }

    return NextResponse.next();
}

export const config = {
    matcher: [
        '/reservations/:path*',
        '/screenings/:path*',
        '/mypage',
    ],
};