import {ExceptionResponse} from "@/src/type/response/exceptionResponse";

const isServer = typeof window === 'undefined';

type LogLevel = 'debug' | 'info' | 'warn' | 'error';

function formatMessage(level: LogLevel, message: string): string {
    const side = isServer ? 'server' : 'client';
    return `[${level}] ${side} ${message}`;
}

function fromException(error: ExceptionResponse){
    return formatMessage(
        'error',
        `[${error.type}(${error.instance})] ${error.title} : ${error.detail}`
    );
}

export const logger = {
    debug: (message: string) => {
        if(process.env.NODE_ENV === 'development'){
            console.log(formatMessage('debug', message));
        }
    },

    info: (message: string) => {
        console.log(formatMessage('info', message));
    },

    warn: (message: string) => {
        console.log(formatMessage('warn', message));
    },

    error: (message: string) => {
        console.log(formatMessage('error', message));
    },

    apiError: (error ?: ExceptionResponse) => {
        console.log(error ? fromException(error) : 'unknown error');
    }

}