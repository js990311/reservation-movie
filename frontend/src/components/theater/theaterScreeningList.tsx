import getTheaterScreeningAction from "@/src/actions/theaterAction";
import {ScreeningWithMovie} from "@/src/type/screening/screening";
import {CalendarX, Clock} from "lucide-react";
import {Badge} from "@/components/ui/badge";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
import Link from "next/link";

type Props = {
    theaterId: string;
    selectedDate: string;
}

export default async function TheaterScreeningList({theaterId,selectedDate}: Readonly<Props>) {
    const screenings = await getTheaterScreeningAction(theaterId, selectedDate);
    const screeningMap = new Map<string, ScreeningWithMovie[]>();

    screenings.forEach((screening) => {
        const key = screening.title;
        const list = screeningMap.get(key) || [];
        list.push(screening);
        screeningMap.set(key, list);
    });

    if (screenings.length === 0) {
        return (
            <div className="flex flex-col items-center justify-center py-16 border-2 border-dashed rounded-xl bg-slate-50 text-muted-foreground">
                <CalendarX className="w-12 h-12 mb-3 opacity-20" />
                <p className="font-medium text-lg">상영 일정이 없습니다</p>
                <p className="text-sm text-slate-400">{selectedDate}에는 영화가 쉬어가나 봐요 😴</p>
            </div>
        );
    }
    return (
        <div className="space-y-6 animate-in fade-in slide-in-from-bottom-2 duration-500">
            <div className="flex items-center justify-between px-1">
                <h2 className="text-xl font-bold flex items-center gap-2">
                    <Clock className="w-5 h-5 text-primary"/>
                    {selectedDate} 상영 시간표
                </h2>
                <Badge variant="secondary" className="px-3 py-1">
                    총 {screeningMap.size}편
                </Badge>
            </div>

            <div className="grid gap-5">
                {Array.from(screeningMap.entries()).map(([movieTitle, movieList]) => {
                    const movieInfo = movieList[0];

                    // 시간순 정렬
                    movieList.sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime());

                    return (
                        <Card key={movieTitle}
                              className="overflow-hidden border shadow-sm hover:shadow-md transition-all">
                            <CardHeader className="bg-muted/30 py-4 border-b">
                                <CardTitle className="text-base flex items-center justify-between">
                                    <div className="flex items-center gap-3">
                                        <Badge variant="outline"
                                               className="bg-background border-primary/20 text-primary">Movie</Badge>
                                        <span className="text-lg font-bold tracking-tight">{movieTitle}</span>
                                    </div>
                                    <div
                                        className="flex items-center gap-1.5 text-sm font-medium text-muted-foreground bg-white px-2 py-1 rounded-md border">
                                        <Clock className="w-3.5 h-3.5"/>
                                        {movieInfo.duration}분
                                    </div>
                                </CardTitle>
                            </CardHeader>
                            <CardContent className="pt-6 pb-6">
                                <div className="flex flex-wrap gap-3">
                                    {movieList.map((sc) => {
                                        const startDate = new Date(sc.startTime);
                                        const timeString = startDate.toLocaleTimeString('ko-KR', {
                                            hour: '2-digit',
                                            minute: '2-digit',
                                            hour12: false
                                        });

                                        return (
                                            <Button
                                                key={sc.screeningId}
                                                variant="outline"
                                                className="h-auto py-2.5 px-5 flex flex-col gap-1 border-slate-200 hover:border-primary hover:bg-primary/5 hover:text-primary transition-all group"
                                                asChild
                                            >
                                                <Link href={`/screenings/${sc.screeningId}`}>
                                                    <span className="font-bold text-lg leading-none">
                                                        {timeString}
                                                    </span>
                                                    <span
                                                        className="text-[10px] text-muted-foreground group-hover:text-primary/70 font-normal">
                                                        예매가능
                                                    </span>
                                                </Link>
                                            </Button>
                                        );
                                    })}
                                </div>
                            </CardContent>
                        </Card>
                    );
                })}
            </div>
        </div>)
}