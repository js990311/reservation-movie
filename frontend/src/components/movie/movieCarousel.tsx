"use client"

import {Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious} from "@/components/ui/carousel";
import {Movie} from "@/src/type/movie/movie";
import MovieCard from "@/src/components/movie/movieCard";

type MovieCarouselProps = {
    movies : Movie[];
}

export default function MovieCarousel({movies}: Readonly<MovieCarouselProps>) {
    return (
        <Carousel className="w-full">
            <CarouselContent className={"w-full"}>
                {
                    movies.map((movie) => (
                        <CarouselItem key={movie.movieId} className={"basis-1/3"}>
                            <MovieCard movie={movie} />
                        </CarouselItem>
                    ))
                }
            </CarouselContent>
            <CarouselPrevious />
            <CarouselNext />
        </Carousel>
    );
}