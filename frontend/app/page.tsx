import MovieCarousel from "@/src/components/movie/movieCarousel";
import Link from "next/link";
import {getMoviesAction} from "@/src/actions/movieAction";


export default async function Home() {
    const response = await getMoviesAction(0, 10);

    if(!response.ok){
        return (
            <div>
                영화정보가 없습니다
            </div>
        )
    }

    const {data: movies} = response;

    return (
        <div className={"w-full"}>
          <div className={"w-full flex justify-between pb-5"}>
            <div className={"font-bold text-2xl"}>
                상영중인 영화
            </div>
            <div>
                <Link href={"/movies"}>
                    더 많은 영화보기
                </Link>
            </div>
          </div>
          <MovieCarousel movies={movies}/>
        </div>
    );
}
