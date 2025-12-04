import getTheaterScreeningAction from "@/src/actions/theaterAction";
import {ScreeningWithTheater} from "@/src/type/screening/screening";
import {getMovieScreeningAction} from "@/src/actions/movieAction";
import {CalendarX, MapPin} from "lucide-react";
import {Badge} from "@/components/ui/badge";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
import Link from "next/link";

type Props = {
    movieId: string;
    selectedDate: string;
}

export async function MovieScreeningList({movieId, selectedDate}: Readonly<Props>) {
    const theaters:ScreeningWithTheater[] = await getMovieScreeningAction(movieId, selectedDate);
    const theaterMap = new Map<string, ScreeningWithTheater[]>();

    theaters.forEach(theater => {
        const key = theater.theaterName;
        const list = theaterMap.get(key) || [];
        list.push(theater);
        theaterMap.set(key, list);
    });

    console.log(theaterMap);

    if (theaters.length === 0) {
        return (
            <div className="flex flex-col items-center justify-center py-16 border-2 border-dashed rounded-xl bg-slate-50 text-muted-foreground">
                <CalendarX className="w-12 h-12 mb-3 opacity-20" />
                <p>선택한 날짜에 상영하는 극장이 없습니다.</p>
            </div>
        );
    }

    return (
        <div className="space-y-6 animate-in fade-in slide-in-from-bottom-2 duration-500">
            {/* 헤더 */}
            <div className="flex items-center justify-between px-1">
                <h2 className="text-xl font-bold flex items-center gap-2">
                    <MapPin className="w-5 h-5 text-primary"/> 상영 극장
                </h2>
                <Badge variant="secondary" className="px-3 py-1">
                    총 {theaterMap.size}개 극장
                </Badge>
            </div>

            {/* 극장별 카드 리스트 */}
            <div className="grid gap-5">
                {Array.from(theaterMap.entries()).map(([theaterName, list]) => {
                    // 시간순 정렬
                    list.sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime());

                    return (
                        <Card key={theaterName}
                              className="overflow-hidden border shadow-sm hover:shadow-md transition-all">
                            <CardHeader className="bg-muted/30 py-4 border-b">
                                <CardTitle className="text-base flex items-center gap-2">
                                    <Badge variant="outline"
                                           className="bg-background border-primary/20 text-primary">Theater</Badge>
                                    <span className="text-lg font-bold tracking-tight">{theaterName}</span>
                                </CardTitle>
                            </CardHeader>
                            <CardContent className="pt-6 pb-6">
                                <div className="flex flex-wrap gap-3">
                                    {list.map((sc) => {
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
                                                        예매
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
        </div>
    );
}