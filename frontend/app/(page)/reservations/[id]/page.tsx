import {Card, CardContent, CardFooter, CardHeader, CardTitle} from "@/components/ui/card";
import {Badge} from "@/components/ui/badge";
import {Label} from "@/components/ui/label";
import {Armchair, CalendarClock, MapPin} from "lucide-react";
import {Button} from "@/components/ui/button";
import {formatDate} from "@/src/components/utils/time/formatter";
import ReservationStatusBadge from "@/src/components/reservation/reservationStatusBadge";
import {clsx} from "clsx";
import Link from "next/link";
import {getReservationIdAction} from "@/src/actions/reservationAction";
import ReservationCancelButton from "@/src/components/reservation/reservationCancelButton";

type Props = {
    params: Promise<{id:string}>  
};

export default async function ReservationIdPage({params} : Readonly<Props>) {
    const {id} = await params;
    const response = await getReservationIdAction(id);

    if(!response.ok){
        return (
            <div>
                예매정보가 없습니다.
            </div>
        );
    }

    const {reservation, seats} = response.data;

    return (
        <div className="flex justify-center py-10">
            <Card className="w-full max-w-lg shadow-lg">
                <CardHeader className="border-b pb-4">
                    <div className="flex justify-between items-center">
                        <CardTitle className="text-2xl font-bold">예매 상세 정보</CardTitle>
                        <ReservationStatusBadge status={reservation.status}></ReservationStatusBadge>
                    </div>
                    <p className="text-sm text-muted-foreground mt-2">
                        예매 번호: <span className="font-mono text-foreground">#{reservation.reservationId}</span>
                    </p>
                </CardHeader>

                <CardContent className="space-y-6 pt-6">
                    {/* 영화 정보 */}
                    <div className="space-y-2">
                        <Label className="text-base text-muted-foreground">영화</Label>
                        <Link href={`/movies/${reservation.movieId}`}>
                            <div className="text-xl font-semibold leading-none">
                                {reservation.movieTitle}
                            </div>
                        </Link>
                    </div>

                    {/* 상영관 정보 */}
                    <div className="flex items-start gap-3">
                        <MapPin className="w-5 h-5 text-primary mt-0.5"/>
                        <div>
                            <Label className="text-sm text-muted-foreground block mb-1">상영관</Label>
                            <Link href={`/theaters/${reservation.theaterId}`}>
                                <span className="font-medium">{reservation.theaterName}</span>
                            </Link>
                        </div>
                    </div>

                    {/* 시간 정보 */}
                    <div className="flex items-start gap-3">
                        <CalendarClock className="w-5 h-5 text-primary mt-0.5"/>
                        <div>
                            <Label className="text-sm text-muted-foreground block mb-1">상영 시간</Label>
                            <div className="font-medium">
                                {formatDate(reservation.startTime)} ~
                            </div>
                            <div className="text-sm text-muted-foreground">
                                {formatDate(reservation.endTime)}
                            </div>
                        </div>
                    </div>

                    {/* 좌석 정보 */}
                    <div className="flex items-start gap-3">
                        <Armchair className="w-5 h-5 text-primary mt-0.5"/>
                        <div className="w-full">
                            <Label className="text-sm text-muted-foreground block mb-2">선택 좌석 ({seats.length}석)</Label>
                            <div className="flex flex-wrap gap-2">
                                {seats.map((seat, index) => (
                                    <Badge key={`${seat.row},${seat.col}`} variant="outline" className="text-sm py-1 px-3 bg-secondary/30">
                                        {seat.row}행 {seat.col}열
                                    </Badge>
                                ))}
                            </div>
                        </div>
                    </div>
                </CardContent>

                <CardFooter className={clsx(
                    "flex border-t pt-6",
                    {"justify-between" : reservation.status === 'PENDING'},
                    {"justify-end" : reservation.status !== 'PENDING'}
                )}>
                    {
                        reservation.status === 'PENDING' && (
                            <Button className={"cursor-pointer"}>
                                <Link href={`/reservations/${reservation.reservationId}/payments`}>
                                    결제하기
                                </Link>
                            </Button>
                        )
                    }
                    <ReservationCancelButton reservationId={    reservation.reservationId} />
                </CardFooter>
            </Card>
        </div>
    );
} 