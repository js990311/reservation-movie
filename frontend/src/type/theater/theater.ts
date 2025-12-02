export type Theater = {
    theaterId: number;
    name: string;
    rowSize: number;
    colSize: number;
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
    rowSize: number;
    colSize: number;
}
