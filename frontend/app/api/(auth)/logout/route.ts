import {NextRequest, NextResponse} from "next/server";
import {clearTokens} from "@/src/lib/api/tokenUtil";
import {clientException, ExceptionResponse} from "@/src/type/exception/exceptionResponse";

export async function POST(){
    try {
        clearTokens();
        return new NextResponse(null, {status: 204});
    }catch (error){
        const exceptionResponse: ExceptionResponse = clientException('/api/logout', error);
        console.error(`/api/logout (${exceptionResponse.status}) : ${exceptionResponse.type}, ${exceptionResponse.detail}`);
        return NextResponse.json(exceptionResponse, {status: 500});
    }
}