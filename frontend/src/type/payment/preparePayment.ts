export type PaymentPrepare = {
    paymentId: string;
    totalAmount: number;
    customData:customData;
}

type customData = {
    reservationId: number;
}