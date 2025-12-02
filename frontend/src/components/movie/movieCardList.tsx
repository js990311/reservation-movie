"use client"

import {Movie} from "@/src/type/movie/movie";
import {useState} from "react";
import MovieCard from "@/src/components/movie/movieCard";
import {Button} from "@/components/ui/button";
import {PaginationMetadata} from "@/src/type/response/pagination";
import {clsx} from "clsx";
import {getMoviesAction} from "@/src/actions/movieAction";
import toast from "react-hot-toast";

interface MovieCardListProps {
    initMovies: Movie[];
    initPagination: PaginationMetadata;
}

export default function MovieCardList({initMovies, initPagination}: Readonly<MovieCardListProps>) {
    const [movies, setMovies] = useState<Movie[]>(initMovies);
    const [pagination, setPagination] = useState<PaginationMetadata>(initPagination);
    const [loading, setLoading] = useState<boolean>(false);

    const handleLoadMovie = async () => {
        if(!pagination.hasNextPage || loading){
            return;
        }

        setLoading(true);
        try {
            const nextPageNumber = pagination.requestNumber;
            const response = await getMoviesAction(nextPageNumber, pagination.requestSize);
            setMovies(prev => [...prev, ...response.data]);
            setPagination(response.pagination);
        }catch (error) {
            toast.error(`영화 불러오기 실패`);
        }finally {
            setLoading(false);
        }
    }

    return (
        <div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 pb-9">
                {movies.map((movie) => (
                    <MovieCard key={movie.movieId} movie={movie}/>
                ))}
            </div>
            <Button className={clsx(
                    "w-full",
                    {"cursor-pointer" : pagination.hasNextPage},
                    {"bg-gray-500 hover:bg-gray-500" : !pagination.hasNextPage}
                )}
                onClick={handleLoadMovie}
            >
                영화 더 보기 <span>{`${pagination.requestNumber} / ${pagination.totalPage}`}</span>
            </Button>
        </div>
    );
}