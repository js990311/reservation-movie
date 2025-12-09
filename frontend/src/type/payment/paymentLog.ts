export type PaymentLog = {
    paymentId: string;
    status: 'PAID' | 'FAILED' | 'CANCELLED';
    reservationId: number;
}