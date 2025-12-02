import MovieCarousel from "@/src/components/movie/movieCarousel";
import {ProxyRequestBuilder} from "@/src/lib/api/proxyRequestBuilder";
import {PaginationResponse} from "@/src/type/response/pagination";
import {Movie} from "@/src/type/movie/movie";
import Link from "next/link";

async function getMovies() {
    try {
        const response = await new ProxyRequestBuilder('/movies').withMethod("GET").execute();
        if(!response.ok) {
            return [];
        }
        const respData: PaginationResponse<Movie> = await response.json();
        return respData.data;
    }catch (error) {
        return [];
    }
}

export default async function Home() {
    const movies = await getMovies();
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
