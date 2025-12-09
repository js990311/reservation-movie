"use server"

import {PaymentLog} from "@/src/type/payment/paymentLog";
import {createInternalServerException} from "@/src/type/error/ApiError";
import {serverFetch} from "@/src/lib/api/serverFetch";
import {BaseResponse} from "@/src/type/response/base";

export async function paymentCompleteAction(paymentId: string): Promise<BaseResponse<PaymentLog>>{
    try {
        const response : BaseResponse<PaymentLog>= await serverFetch<PaymentLog>({
            endpoint: "/payments/complete",
            method: "POST",
            withAuth: true,
            body: {paymentId: paymentId},
        })
        if(response.data){
            return response;
        }else {
            return {
                data: {
                    paymentId: paymentId,
                    status: "FAILED",
                    reservationId: 0
                },
                error: response.error,
            };
        }
    }catch (error) {
        return {
            data: {
                paymentId: paymentId,
                status: "FAILED",
                reservationId: 0
            },
            error: createInternalServerException('paymentCompleteAction', error)
        };
    }
}

export async function getPaymentsAction(page: number, size:number): Promise<BaseResponse<PaymentLog[]>>{
    try {
        const response = await serverFetch<PaymentLog[]>({
            endpoint: `/payments?page=${page}&size=${size}`,
            method: "GET",
            withAuth:true
        });
        return response;
    }catch (error) {
        return {
            data: [],
            pagination: {
                count: 0,
                requestNumber: page,
                requestSize: size,
                hasNextPage: false,
                totalPage: 0,
                totalElements : 0
            },
            error: createInternalServerException('getPaymentsAction', error)
        }
    }
}