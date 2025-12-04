import {Screening} from "@/src/type/screening/screening";
import {Movie} from "@/src/type/movie/movie";
import {Seat, Theater} from "@/src/type/theater/theater";

export type ScreeningDetail = {
    screening: Screening;
    movie: Movie;
    theater: Theater;
    seats: Seat[];
}