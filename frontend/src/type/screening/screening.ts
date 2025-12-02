export type Screening = {
    theaterId: number;
    screeningId: number;
    movieId: number;
    startTime: string;
    endTime: string;
}

export type ScreeningWithMovie = {
    theaterId: number;
    screeningId: number;
    startTime: string;
    endTime: string;
    movieId: number;
    title: string;
    duration: number;
}