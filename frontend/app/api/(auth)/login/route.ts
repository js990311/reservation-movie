import {NextRequest, NextResponse} from "next/server";
import {ProxyRequestBuilder} from "@/src/lib/api/proxyRequestBuilder";
import {Tokens} from "@/src/type/token/tokens";
import {setTokens} from "@/src/lib/api/tokenUtil";
import {clientException, ExceptionResponse} from "@/src/type/exception/exceptionResponse";

export async function POST(request: NextRequest){
    try {
        const body = await request.json();

        const response = await new ProxyRequestBuilder('/login')
            .withMethod('POST')
            .withBody(body)
            .execute();

        if(!response.ok){
            return response;
        }

        const token:Tokens = await response.json();
        await setTokens(token);

        return NextResponse.json({ok: true}, {status: 200});
    }catch (error){
        const exceptionResponse: ExceptionResponse = clientException('/api/login', error);
        console.error(`/api/login (${exceptionResponse.status}) : ${exceptionResponse.type}, ${exceptionResponse.detail}`);
        return NextResponse.json(exceptionResponse, {status: 500});
    }
}