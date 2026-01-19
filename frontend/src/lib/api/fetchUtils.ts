export function toQuery(params?: Record<string, any>): string {
    if(!params) {
        return '';
    }
    const usp = new URLSearchParams(params);
    for(const [key, value] of Object.entries(params)) {
        usp.append(key, value);
    }
    const s = usp.toString();
    return s ? `?${s}` : '';
}

export async function safeToJson<T>(response: Response): Promise<T | undefined> {
    const ct = response.headers.get('content-type') ?? '';
    if (!ct.includes('application/json'))
        return undefined;
    try {
        return (await response.json()) as T
    } catch {
        return undefined
    }
}

type SetHeaderParams = {
    headers?: Record<string, string>;
    withAuth?:boolean;
}

export async function setHeader({headers, withAuth} : SetHeaderParams) : Promise<Record<string, string>>{
    const requestHeaders : Record<string, string>= {
        'Content-Type': 'application/json',
        ...headers,
    };

    if(withAuth){
        const accessToken = '';
        if(accessToken){
            requestHeaders['Authorization'] = `Bearer ${accessToken}`;
        }
    }

    return requestHeaders;
}
