import {ProxyRequestBuilder} from "@/src/lib/api/proxyRequestBuilder";
import {BaseResponse} from "@/src/type/response/base";
import {Movie} from "@/src/type/movie/movie";
import {Clock, Film} from "lucide-react";
import DateSelector from "@/src/components/date/dateSelector";
import {MovieScreeningList} from "@/src/components/movie/movieScreeningList";

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
            {/* 1. 영화 정보 헤더 */}
            <div className="flex flex-col md:flex-row gap-8 mb-12">
                {/* 포스터 영역 (Placeholder) */}
                <div className="w-48 md:w-64 aspect-[2/3] bg-slate-200 rounded-lg shadow-lg flex items-center justify-center text-slate-400 shrink-0 mx-auto md:mx-0">
                    <Film className="w-16 h-16" />
                </div>

                {/* 정보 영역 */}
                <div className="flex-1 flex flex-col justify-center text-center md:text-left space-y-4">
                    <div className="space-y-2">
                        <h1 className="text-4xl md:text-5xl font-extrabold tracking-tight text-slate-900">{movie.title}</h1>
                    </div>

                    <div className="flex items-center justify-center md:justify-start gap-4 text-muted-foreground text-lg">
                        <span className="flex items-center gap-1">
                            <Clock className="w-5 h-5" /> {movie.duration}분
                        </span>
                        <span>•</span>
                        <span> 장르 </span>
                    </div>

                    <p className="text-slate-600 max-w-2xl mx-auto md:mx-0 leading-relaxed">
                        세부설명
                    </p>
                </div>
            </div>
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
