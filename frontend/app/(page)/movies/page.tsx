import {Movie} from "@/src/type/movie/movie";
import MovieCardList from "@/src/components/movie/movieCardList";
import {BaseResponse} from "@/src/type/response/base";
import {getMoviesAction} from "@/src/actions/movieAction";

export default async function MoviePage(){
    const {data, pagination}: BaseResponse<Movie[]> = await getMoviesAction(0, 10);

    let movie:Movie[] = [];
    if(data){
        movie = data;
    }

    return (
        <div>
            <div className="space-y-6">
                <div className="flex items-center justify-between">
                    <h1 className="text-3xl font-bold tracking-tight">현재 상영작</h1>
                </div>

                {movie.length === 0 ? (
                    <div className="text-center py-20 text-muted-foreground">
                        현재 상영 중인 영화가 없습니다. 😭
                    </div>
                ) : (
                    <MovieCardList initMovies={movie} initPagination={pagination} />
                )}
            </div>
        </div>
    );
}