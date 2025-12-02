import {ProxyRequestBuilder} from "@/src/lib/api/proxyRequestBuilder";
import {PaginationResponse} from "@/src/type/response/pagination";
import {Movie} from "@/src/type/movie/movie";
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card";
import {Clock} from "lucide-react";
import {Button} from "@/components/ui/button";
import Link from "next/link";
import MovieCard from "@/src/components/movie/movieCard";
import MovieCardList from "@/src/components/movie/movieCardList";

async function getMovies() {
    try {
        const response = await new ProxyRequestBuilder('/movies').withMethod("GET").execute();
        if(!response.ok) {
            return [];
        }
        const respData: PaginationResponse<Movie> = await response.json();
        return respData;
    }catch (error) {
        return [];
    }
}

export default async function MoviePage(){
    const {data, pagination}: PaginationResponse<Movie> = await getMovies();
    return (
        <div>
            <div className="space-y-6">
                <div className="flex items-center justify-between">
                    <h1 className="text-3xl font-bold tracking-tight">현재 상영작</h1>
                </div>

                {data.length === 0 ? (
                    <div className="text-center py-20 text-muted-foreground">
                        현재 상영 중인 영화가 없습니다. 😭
                    </div>
                ) : (
                    <MovieCardList initMovies={data} initPagination={pagination} />
                )}
            </div>
        </div>
    );
}