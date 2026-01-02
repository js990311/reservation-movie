"use client"

import {Theater, Seat} from "@/src/type/theater/theater";
import SeatMap from "@/src/components/theater/seatMap";
import {useState, useMemo} from "react"; // useMemo 추가
import {Button} from "@/components/ui/button";
import {reservationAction} from "@/src/actions/reservationAction";
import toast from "react-hot-toast";
import {useRouter} from "next/navigation";
import {Screening} from "@/src/type/screening/screening";

type Props = {
    screening: Screening;
    theater: Theater;
    seats: Seat[];
}

export default function ScreeningReservation({screening, theater, seats}: Readonly<Props>) {
    const [selectedSeatIds, setSelectedSeatIds] = useState<number[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const router = useRouter();

    // 상영 종료 여부 계산 (현재 시간과 비교)
    const isExpired = useMemo(() => {
        return new Date(screening.startTime) < new Date();
    }, [screening.startTime]);

    const startTimeStr = new Date(screening.startTime).toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
    });
    const endTimeStr = new Date(screening.endTime).toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
    });

    const onSeatClick = (seatId: number) => {
        if(loading || isExpired) { // 종료된 영화는 클릭 방지
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
        if(loading || isExpired) return;

        if(selectedSeatIds.length === 0) {
            toast.error("한 개 이상의 좌석을 선택해주십시오");
            return;
        }

        setLoading(true); // 로딩 시작
        try {
            const response = await reservationAction({
                screeningId: screening.screeningId,
                seats: selectedSeatIds
            });
            if(!response.ok){
                toast.error(`${response.error.title}`);
            }else {
                toast.success('예매 성공!');
                router.push(`/reservations/${response.data.reservationId}/payments`);
            }
        }catch (error) {
            toast.error(`시스템 오류가 발생했습니다.`);
        }finally {
            setLoading(false);
        }
    }

    return (
        <div className="flex flex-col gap-4 p-4">
            {/* 상영 시간 정보 표시 */}
            <div className="flex justify-between items-center bg-secondary p-3 rounded-lg">
                <div className="flex flex-col">
                    <span className="text-sm text-muted-foreground">상영 시간</span>
                    <span className="font-bold text-lg">
                        {startTimeStr} ~ {endTimeStr}
                    </span>
                </div>
                {isExpired && (
                    <span className="text-destructive font-bold">상영이 종료되었습니다.</span>
                )}
            </div>

            <SeatMap
                seats={seats}
                rowSize={theater.rowSize}
                colSize={theater.colSize}
                onSeatClick={onSeatClick}
                selectedSeatIds={selectedSeatIds}
                disabled={isExpired}
            />

            {/* 상영 시간이 지나면 버튼 숨기기 또는 비활성화 */}
            {!isExpired ? (
                <Button
                    onClick={reservationHandler}
                    disabled={loading || selectedSeatIds.length === 0}
                    className="w-full"
                >
                    {loading ? "예매 중..." : "예매하기"}
                </Button>
            ) : (
                <Button disabled className="w-full bg-gray-400">
                    예매 불가
                </Button>
            )}
        </div>
    )
}