import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card";
import {Clock} from "lucide-react";
import {Button} from "@/components/ui/button";
import Link from "next/link";
import {Movie} from "@/src/type/movie/movie";

type MovieCardProps = {
    movie: Movie;
}

export default function MovieCard({movie}: Readonly<MovieCardProps>) {
return (
    <Card key={movie.movieId}
    className="flex flex-col h-full hover:shadow-lg transition-shadow">
    <CardHeader>
        <CardTitle className="line-clamp-1">{movie.title}</CardTitle>
        <CardDescription className="flex items-center gap-1">
            <Clock className="w-4 h-4"/> {movie.duration}분
        </CardDescription>
    </CardHeader>
    <CardContent className="flex-1">
        {/* 포스터 이미지가 있다면 여기에 추가 */}
        <div
            className="aspect-[2/3] bg-muted rounded-md flex items-center justify-center text-muted-foreground mb-4">
            No Poster
        </div>
    </CardContent>
    <CardFooter>
        <Button asChild className="w-full">
        <Link href={`/movies/${movie.movieId}`}>
            예매하기
        </Link>
        </Button>
    </CardFooter>
    </Card>
);
}