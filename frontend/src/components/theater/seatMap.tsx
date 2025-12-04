"use client"
import {Seat} from "@/src/type/theater/theater";
import {Button} from "@/components/ui/button";
import {clsx} from "clsx";

interface SeatMapProps{
    seats: Seat[];
    onSeatClick?: (seatId: number) => void;
    rowSize: number;
    colSize: number;
    selectedSeatIds ?: number[];
}

interface SeatProps {
    seat: Seat;
    status: 'AVAILABLE' | 'SELECTED' | 'RESERVED';
    onClick?: (seatId: number) => void;
    disabled?: boolean;
}

export default function SeatMap({ seats, onSeatClick, rowSize, colSize, selectedSeatIds }: Readonly<SeatMapProps>) {

    const checkStatus = (seat: Seat) => {
        if(seat.reserved){
            return 'RESERVED';
        }else if(selectedSeatIds?.includes(seat.seatId)){
            return 'SELECTED';
        }else {
            return 'AVAILABLE';
        }
    }

    return (
        <div className="w-full overflow-auto p-10 bg-slate-50 rounded-xl border">
            <div className="mb-12 w-full flex flex-col items-center gap-2">
                <div className="h-2 w-3/4 bg-slate-300 rounded-full shadow-[0_20px_40px_-10px_rgba(0,0,0,0.3)]"></div>
                <span className="text-sm text-slate-400 font-medium tracking-[0.5em]">SCREEN</span>
            </div>

            {/* 좌석 그리드 영역 */}
            <div
                className="grid gap-2 mx-auto w-fit"
                style={{
                    // CSS 변수로 그리드 크기 동적 할당
                    gridTemplateRows: `repeat(${rowSize}, min-content)`,
                    gridTemplateColumns: `repeat(${colSize}, min-content)`
                }}
            >
                {seats.map((seat) => (
                    <SeatUnit
                        key={seat.seatId}
                        status={checkStatus(seat)}
                        seat={seat}
                        onClick={onSeatClick}
                    />
                ))}
            </div>
        </div>
    );
}

function SeatUnit({
                      seat,
                      status,
                      onClick,
                      disabled = false
                  }: Readonly<SeatProps>) {

    // 상태별 스타일 정의
    const statusStyles = {
        AVAILABLE: "bg-white border-2 border-slate-300 hover:border-primary hover:bg-primary/10 text-slate-700",
        SELECTED: "bg-green-500 border-2 border-green-600 text-white shadow-md shadow-green-200",
        RESERVED: "bg-slate-200 border-none text-slate-400 cursor-not-allowed",
    };

    const onClickHandler = () => {
        if(onClick){
            onClick(seat.seatId);
        }
    }

    return (
        <Button
            type="button"
            disabled={disabled || status === 'RESERVED'}
            onClick={onClickHandler}
            className={clsx(
                "relative flex h-8 w-8 items-center justify-center rounded-t-lg rounded-b-sm text-xs font-bold transition-all duration-200",
                statusStyles[status]
            )}
            style={{
                gridRow: seat.row,
                gridColumn: seat.col,
            }}
            aria-label={`${seat.row}행 ${seat.col}열 좌석 ${status === 'RESERVED' ? '예약됨' : ''}`}
        >
        {/* 좌석 번호 표시 (선택사항) */}
        <span className="z-10">{seat.col}</span>

        {/* 좌석 입체감용 하단 바 (장식) */}
        <div className={clsx(
            "absolute -bottom-1 h-1 w-6 rounded-b-md",
            {
                "bg-green-700": status === 'SELECTED',
                "bg-slate-300": status !== 'SELECTED',
            }
        )} />
    </Button>
);
}