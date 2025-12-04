import {Clock, Film} from "lucide-react";
import {Movie} from "@/src/type/movie/movie";

type Props = {
    movie: Movie
}

export default function MovieInfo({movie} : Props){
    return (
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
    );
}