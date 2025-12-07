export type ReservationRequest = {
    screeningId: number;
    seats: number[];
}

export type Reservation = {
    reservationId: number;
    status: ReservationStatus;
    userId: number;
    screeningId: number;
    reservationSeats: ReservationSeat[];
}

export type ReservationSeat = {
    reservationSeatId: number;
    seatId: number;
    reservationId: number;
}

export type ReservationDetail = {
    reservation: ReservationSummary;
    seats: ReservationSeatNumber[];
}

export type ReservationStatus = "PENDING" | "CONFIRMED" | "CANCELED" | "COMPLETED";

export type ReservationSummary = {
    reservationId: number;
    status: ReservationStatus;
    totalAmount: number;
    screeningId: number;
    startTime: string;
    endTime: string;
    movieId: number;
    movieTitle: string;
    theaterId: number;
    theaterName: string;
}

export type ReservationSeatNumber = {
    row: number;
    col: number;
}