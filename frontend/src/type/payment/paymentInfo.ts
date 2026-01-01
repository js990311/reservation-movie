import {PaymentCancelStatus, PaymentStatus} from "@/src/type/payment/paymentStatus";

export type PaymentInfo = {
    paymentUid:string;
    paymentStatus:PaymentStatus;
    reservationId:number;
    createAt:string;
    cancelInfo: PaymentCancelInfo;
}

export type PaymentCancelInfo = {
    cancelStatus: PaymentCancelStatus;
    canceledAt:string;
    cancelReason:string;
}

