"use server"

import {PaymentLog} from "@/src/type/payment/paymentLog";
import {ActionOneResult, failResult, oneResult} from "@/src/type/response/result";
import {fetchOne} from "@/src/lib/api/fetchWrapper";
import {BaseError, unknownFetchException} from "@/src/lib/api/error/apiErrors";
import {PaymentPrepare} from "@/src/type/payment/preparePayment";

export async function paymentCompleteAction(paymentId: string): Promise<ActionOneResult<PaymentLog>>{
    try {
        const response = await fetchOne<PaymentLog>({
            endpoint: "/payments/complete",
            method: "POST",
            withAuth: true,
            body: {paymentId: paymentId},
        });
        return oneResult(response);
    }catch (error) {
        if(error instanceof BaseError){
            return failResult(error.details);
        }else {
            const exception = unknownFetchException('paymentCompleteAction',error);
            return failResult(exception.details);
        }
    }
}

export async function getPaymentPrepare(reservationId: number): Promise<ActionOneResult<PaymentPrepare>> {
    try {
        const response = await fetchOne<PaymentPrepare>({
            endpoint: `/reservations/${reservationId}/payments/prepare`,
            method: "POST",
            withAuth:true
        });
        return oneResult(response);
    }catch (error) {
        if(error instanceof BaseError){
            return failResult(error.details);
        }else {
            const exception = unknownFetchException('getPaymentPrepare',error);
            return failResult(exception.details);
        }
    }

}