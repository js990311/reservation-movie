"use server"

import {ProxyRequestBuilder} from "@/src/lib/api/proxyRequestBuilder";
import {BaseResponse} from "@/src/type/response/base";
import {PaymentLog} from "@/src/type/payment/paymentLog";

export async function paymentCompleteAction(paymentId: string){
    try {
        const response = await new ProxyRequestBuilder("/payment/complete").withAuth().withMethod('POST').withBody({paymentId: paymentId}).execute();
        const respDate: BaseResponse<PaymentLog> = await response.json();
        console.log(respDate);
        return respDate;
    }catch (error) {
        return {
            data: {
                paymentId: paymentId,
                status: "FAILED",
                reservationId: 0
            },
        };
    }
}