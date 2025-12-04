export type ReservationRequest = {
    screeningId: number;
    seats: number[];
}

export type Reservation = {
    reservationId: number;
    status: string;
    userId: number;
    screeningId: number;
    reservationSeats: ReservationSeat[];
}

export type ReservationSeat = {
    reservationSeatId: number;
    seatId: number;
    reservationId: number;
}