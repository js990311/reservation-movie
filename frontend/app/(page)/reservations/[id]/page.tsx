import {ProxyRequestBuilder} from "@/src/lib/api/proxyRequestBuilder";
import {BaseResponse} from "@/src/type/response/base";
import {ReservationDetail} from "@/src/type/reservation/reservation";
import {Card, CardContent, CardFooter, CardHeader, CardTitle} from "@/components/ui/card";
import {Badge} from "@/components/ui/badge";
import {Label} from "@/components/ui/label";
import {Armchair, CalendarClock, ChevronLeft, MapPin} from "lucide-react";
import {Button} from "@/components/ui/button";

type Props = {
    params: Promise<{id:string}>  
};

async function getReservationId(id: string){
    try {
        const response = await new ProxyRequestBuilder(`/reservations/${id}`)
            .withMethod('GET')
            .withAuth()
            .execute();
        if(response.ok){
            const data:BaseResponse<ReservationDetail> = await response.json();
            return data.data;
        }else {
            return null;
        }
    }catch (error){
        return null;
    }
}

export default async function ReservationIdPage({params} : Readonly<Props>) {
    const {id} = await params;
    const reservation = await getReservationId(id);

    // 날짜 포맷팅 헬퍼
    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return new Intl.DateTimeFormat("ko-KR", {
            dateStyle: "medium",
            timeStyle: "short",
        }).format(date);
    }

    if(!reservation){
        return (
            <div>
                예매정보가 없습니다.
            </div>
        );
    }

    return (
        <div className="flex justify-center py-10">
            <Card className="w-full max-w-lg shadow-lg">
                <CardHeader className="border-b pb-4">
                    <div className="flex justify-between items-center">
                        <CardTitle className="text-2xl font-bold">예매 상세 정보</CardTitle>
                    </div>
                    <p className="text-sm text-muted-foreground mt-2">
                        예매 번호: <span className="font-mono text-foreground">#{reservation.reservation.reservationId}</span>
                    </p>
                </CardHeader>

                <CardContent className="space-y-6 pt-6">
                    {/* 영화 정보 */}
                    <div className="space-y-2">
                        <Label className="text-base text-muted-foreground">영화</Label>
                        <div className="text-xl font-semibold leading-none">
                            {reservation.reservation.movieTitle}
                        </div>
                    </div>

                    {/* 상영관 정보 */}
                    <div className="flex items-start gap-3">
                        <MapPin className="w-5 h-5 text-primary mt-0.5"/>
                        <div>
                            <Label className="text-sm text-muted-foreground block mb-1">상영관</Label>
                            <span className="font-medium">{reservation.reservation.theaterName}</span>
                        </div>
                    </div>

                    {/* 시간 정보 */}
                    <div className="flex items-start gap-3">
                        <CalendarClock className="w-5 h-5 text-primary mt-0.5"/>
                        <div>
                            <Label className="text-sm text-muted-foreground block mb-1">상영 시간</Label>
                            <div className="font-medium">
                                {formatDate(reservation.reservation.startTime)} ~
                            </div>
                            <div className="text-sm text-muted-foreground">
                                {formatDate(reservation.reservation.endTime)}
                            </div>
                        </div>
                    </div>

                    {/* 좌석 정보 */}
                    <div className="flex items-start gap-3">
                        <Armchair className="w-5 h-5 text-primary mt-0.5"/>
                        <div className="w-full">
                            <Label className="text-sm text-muted-foreground block mb-2">선택 좌석 ({reservation.seats.length}석)</Label>
                            <div className="flex flex-wrap gap-2">
                                {reservation.seats.map((seat, index) => (
                                    <Badge key={`${seat.row},${seat.col}`} variant="outline" className="text-sm py-1 px-3 bg-secondary/30">
                                        {seat.row}행 {seat.col}열
                                    </Badge>
                                ))}
                            </div>
                        </div>
                    </div>
                </CardContent>

                <CardFooter className="flex justify-end border-t pt-6">
                    <Button className={"cursor-pointer text-red-600 bg-white border-red-600 border hover:bg-red-700 hover:text-white"}>
                        취소하기
                    </Button>
                </CardFooter>
            </Card>
        </div>
    );
} 