export type Theater = {
    theaterId: string;
    name: string;
    rowSize: number;
    colSize: number;
    seats: Seat[];
}

export type Seat = {
    col: number;
    theaterId?: string;
    seatId: string;
    row: number;
    reserved?: boolean;
}

export type TheaterSummary = {
    theaterId: string;
    name: string;
    rowSize: number;
    colSize: number;
}

