"use client"

import {Theater, Seat} from "@/src/type/theater/theater";
import SeatMap from "@/src/components/theater/seatMap";
import {useState} from "react";
import {Button} from "@/components/ui/button";
import {reservationAction} from "@/src/actions/reservationAction";
import toast from "react-hot-toast";
import {useRouter} from "next/navigation";

type Props = {
    screeningId: string;
    theater: Theater;
    seats: Seat[];
}

export default function ScreeningReservation({screeningId, theater, seats}: Readonly<Props>) {
    const [selectedSeatIds, setSelectedSeatIds] = useState<number[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const router = useRouter();
    const onSeatClick = (seatId: number) => {
        if(loading) {
            return;
        }
        setSelectedSeatIds(prev=> {
            if(prev.includes(seatId)){
                return prev.filter(id => id !== seatId);
            }else {
                return [...prev, seatId];
            }
        });
    }

    const reservationHandler = async () => {
        if(loading){
            return;
        }
        if(selectedSeatIds.length === 0) {
            toast.error("한 개 이상의 좌석을 선택해주십시오");
            return;
        }
        try {
            const response = await reservationAction({
                screeningId: parseInt(screeningId),
                seats: selectedSeatIds
            });
            if('type' in response){ // exceptionResponse에 해당
                toast.error(`${response.type} : ${response.title} ${response.detail}`);
            }else {
                toast.success('예매성공');
                router.push(`/reservations/${response.reservationId}`);
            }
        }catch (error) {
            toast.error(`시스템 오류가 발생했습니다.`);
        }finally {
            setLoading(false);
        }
    }

    return (
        <div>
            <SeatMap
                seats={seats}
                rowSize={theater.rowSize}
                colSize={theater.colSize}
                onSeatClick={onSeatClick}
                selectedSeatIds={selectedSeatIds}
            />
            <Button onClick={reservationHandler}>
                예매하기
            </Button>
        </div>
    )
}