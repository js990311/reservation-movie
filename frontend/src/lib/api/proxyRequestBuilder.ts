import {NextResponse} from "next/server";
import {clientException, ExceptionResponse} from "@/src/type/exception/exceptionResponse";
import {getAccessToken} from "@/src/lib/api/tokenUtil";

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE';

export class ProxyRequestBuilder{
    private readonly host = process.env.BACKEND_HOST ?? 'http://localhost:8080/api';
    private endpoint: string;
    private method: HttpMethod = 'GET';
    private body?: unknown;
    private auth: boolean = false;

    constructor(endpoint: string) {
        this.endpoint = endpoint;
    }

    withMethod(method: HttpMethod): this {
        this.method = method;
        return this;
    }

    withBody<B>(body: B): this{
        this.body = body;
        return this;
    }

    withAuth(): this{
        this.auth = true;
        return this;
    }

    async execute() : Promise<NextResponse> {
        try {
            const url = `${this.host}${this.endpoint}`;

            // 헤더 설정
            const headers: Record<string, string> = {
                'Content-Type': 'application/json',
            }

            // 보안 헤더 설정
            if (this.auth) {
                const accessToken = await getAccessToken();
                console.log(`[ProxyRequest] Auth Check - Token Exists: ${!!accessToken}`);
                if (accessToken === null) {
                    return new NextResponse(null, {status: 401});
                }
                headers['Authorization'] = `Bearer ${accessToken}`;
            }

            // 응답 받기
            const response = await this.fetch(url, headers);

            if (response.ok) {
                const text = await response.text();
                if (!text) {
                    return new NextResponse(null, {status: response.status});
                }
                try {
                    const data = JSON.parse(text);
                    return NextResponse.json(data, {status: response.status});
                } catch {
                    return new NextResponse(text, {status: response.status});
                }
            } else {
                const exceptionResponse: ExceptionResponse = await response.json();
                console.error(`${this.endpoint} (${response.status}) : ${exceptionResponse.type}, ${exceptionResponse.detail}`);
                return NextResponse.json(exceptionResponse, {status: response.status});
            }
        } catch (error) {
            const exceptionResponse: ExceptionResponse = clientException(this.endpoint, error);
            console.error(`${this.endpoint} (${exceptionResponse.status}) : ${exceptionResponse.type}, ${exceptionResponse.detail}`);
            return NextResponse.json(exceptionResponse, {status: 500});
        }
    }

    private async fetch(endpoint: string, headers: Record<string, string>){
        return fetch(
            endpoint,
            {
                method: this.method,
                headers: headers,
                body: this.body ? JSON.stringify(this.body) : undefined,
            }
        );
    }
}

