import {ProxyRequestBuilder} from "@/src/lib/api/proxyRequestBuilder";
import {BaseResponse} from "@/src/type/response/base";
import {Movie} from "@/src/type/movie/movie";
import {Clock, Film} from "lucide-react";
import DateSelector from "@/src/components/date/dateSelector";
import {MovieScreeningList} from "@/src/components/movie/movieScreeningList";
import MovieInfo from "@/src/components/movie/movieInfo";

async function getMovieById(id: string) {
    try {
        const response = await new ProxyRequestBuilder(`/movies/${id}`).withMethod("GET").execute();
        if(!response.ok) {
            return null;
        }
        const respData: BaseResponse<Movie> = await response.json();
        return respData.data;
    }catch (error) {
        return null;
    }
}

type Props = {
    params: Promise<{id: string}>;
    searchParams: Promise<{date: string}>;
}

export default async function MovieIdPage({params, searchParams} : Readonly<Props>) {
    const {id} = await params;
    const {date} = await searchParams;
    const selectedDate = date ?? new Date().toISOString().split("T")[0];
    const movie = await getMovieById(id);

    if(!movie) {
        return (
            <div>
                404 영화 정보가 없습니다.
            </div>
        );
    }

    return (
        <div className="container mx-auto py-10 px-4 max-w-5xl">
            <MovieInfo movie={movie} />
            <DateSelector
                selectedDate={selectedDate}
                baseUrl={`/movies/${id}`}
            ></DateSelector>
            <MovieScreeningList
                movieId={id}
                selectedDate={selectedDate}
            />
        </div>
    );

}
