export type ReservationRequest = {
    screeningId: string;
    seats: number[];
}

export type Reservation = {
    reservationId: string;
    status: ReservationStatus;
    userId: string;
    screeningId: string;
    reservationSeats: ReservationSeat[];
}

export type ReservationSeat = {
    reservationSeatId: string;
    seatId: string;
    reservationId: string;
}

export type ReservationDetail = {
    reservation: ReservationSummary;
    seats: ReservationSeatNumber[];
}

export type ReservationStatus = "PENDING" | "CONFIRMED" | "CANCELED" | "COMPLETED";

export type ReservationSummary = {
    reservationId: string;
    status: ReservationStatus;
    totalAmount: number;
    screeningId: string;
    startTime: string;
    endTime: string;
    movieId: string;
    movieTitle: string;
    theaterId: string;
    theaterName: string;
}

export type ReservationSeatNumber = {
    row: number;
    col: number;
}