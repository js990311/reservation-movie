import DateSelector from "@/src/components/date/dateSelector";
import {MovieScreeningList} from "@/src/components/movie/movieScreeningList";
import MovieInfo from "@/src/components/movie/movieInfo";
import {getMovieByIdAction} from "@/src/actions/movieAction";

type Props = {
    params: Promise<{id: string}>;
    searchParams: Promise<{date: string}>;
}

export default async function MovieIdPage({params, searchParams} : Readonly<Props>) {
    const {id} = await params;
    const {date} = await searchParams;
    const selectedDate = date ?? new Date().toISOString().split("T")[0];
    const result = await getMovieByIdAction(id);

    if(!result.ok) {
        return (
            <div>
                404 영화 정보가 없습니다.
            </div>
        );
    }

    const movie = result.data;

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
