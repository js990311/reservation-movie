export type Screening = {
    theaterId: string;
    screeningId: string;
    movieId: string;
    startTime: string;
    endTime: string;
}

export type ScreeningWithMovie = {
    theaterId: string;
    screeningId: string;
    startTime: string;
    endTime: string;
    movieId: string;
    title: string;
    duration: number;
}

export type ScreeningWithTheater = {
    screeningId: string;
    startTime: string;
    endTime: string;
    movieId: string;
    theaterId: string;
    theaterName: string;
}

