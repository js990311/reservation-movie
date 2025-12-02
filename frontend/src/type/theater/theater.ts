export type Theater = {
    theaterId: number;
    name: string;
    seats: Seat[];
}

export type Seat = {
    col: number;
    theaterId: number;
    seatId: number;
    row: number;
}

export type TheaterSummary = {
    theaterId: number;
    name: string;
}
