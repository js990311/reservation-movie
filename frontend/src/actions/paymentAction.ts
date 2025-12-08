"use server"

import {PaymentLog} from "@/src/type/payment/paymentLog";
import {createInternalServerException} from "@/src/type/error/ApiError";
import {serverFetch} from "@/src/lib/api/serverFetch";

export async function paymentCompleteAction(paymentId: string){
    try {
        return serverFetch<PaymentLog>({
            endpoint: "/payment/complete",
            method: "POST",
            withAuth: true,
            body: {paymentId: paymentId},
        })
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