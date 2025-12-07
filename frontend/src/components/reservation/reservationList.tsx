"use client"

import {Button} from "@/components/ui/button";
import {CalendarIcon, ClockIcon, MapPinIcon} from "lucide-react";
import PaginationRemote from "@/src/components/pagination/paginationRemote";
import {PaginationMetadata} from "@/src/type/response/pagination";
import {ReservationSummary} from "@/src/type/reservation/reservation";
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card";
import {formatDate} from "@/src/components/utils/time/formatter";
import Link from "next/link";
import ReservationStatusBadge from "@/src/components/reservation/reservationStatusBadge";

type Props = {
    reservations: ReservationSummary[];
}

export default function ReservationList({ reservations }: Readonly<Props>) {
    return (
        <div>
            <div className="grid gap-4">
                {reservations.map((res) => (
                    <Card key={res.reservationId} className="overflow-hidden transition-all hover:shadow-md">
                        <Link href={`/reservations/${res.reservationId}`}>
                        <CardHeader className="pb-3 bg-muted/30 border-b">
                                <div className="flex justify-between items-center">
                                    <div className="space-y-1">
                                        <CardTitle className="text-xl flex items-center gap-2">
                                            {res.movieTitle}
                                        </CardTitle>
                                        <CardDescription>
                                            #{res.reservationId}
                                        </CardDescription>
                                    </div>
                                    <ReservationStatusBadge status={res.status}></ReservationStatusBadge>
                                </div>
                        </CardHeader>
                        <CardContent className="pt-4 grid gap-3 sm:grid-cols-2">
                            <div className="flex items-center gap-2 text-sm">
                                <MapPinIcon className="w-4 h-4 text-primary"/>
                                <span className="font-medium">{res.theaterName}</span>
                            </div>
                            <div className="flex items-center gap-2 text-sm">
                                <CalendarIcon className="w-4 h-4 text-muted-foreground"/>
                                <span>시작: {formatDate(res.startTime)}</span>
                            </div>
                            <div className="flex items-center gap-2 text-sm sm:col-start-2">
                                <ClockIcon className="w-4 h-4 text-muted-foreground"/>
                                <span>종료: {formatDate(res.endTime)}</span>
                            </div>
                        </CardContent>
                        </Link>
                    </Card>
                ))}
            </div>
        </div>
    );
}