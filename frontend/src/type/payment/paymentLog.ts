import {PaymentStatus} from "@/src/type/payment/paymentStatus";

export type PaymentLog = {
    paymentId: string;
    status: PaymentStatus;
    reservationId: number;
}

