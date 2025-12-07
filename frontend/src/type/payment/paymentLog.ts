export type PaymentLog = {
    paymentId: string;
    status: 'PAID' | 'FAILED';
    reservationId: number;
}