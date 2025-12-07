"use client"

import { ReservationDetail } from "@/src/type/reservation/reservation";
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/components/ui/badge";
import { CalendarIcon, ClockIcon, CreditCardIcon, FilmIcon, MapPinIcon, ArmchairIcon } from "lucide-react";
import usePayment from "@/src/hooks/paymentHook";
import {useEffect} from "react";
import toast from "react-hot-toast";
import {useRouter} from "next/navigation";

// Props 정의: 데이터와 동작(함수)을 받습니다.
interface PaymentSummaryCardProps {
    reservationDetail: ReservationDetail;
}

export default function PaymentSummaryCard({ reservationDetail }: PaymentSummaryCardProps) {
    const { reservation, seats } = reservationDetail;
    const {paymentStatus,handlePayment} = usePayment();
    const router = useRouter();

    useEffect(() => {
        if(paymentStatus.status === 'FAILED'){
            toast.error(`Payment failed : ${paymentStatus.message}`);
        }else if(paymentStatus.status === 'SUCCESS'){
            toast.success("Payment successful");
            router.push(`/reservations/${reservation.reservationId}`);
        }
    }, [paymentStatus]);

    // 날짜 및 시간 포맷팅
    const startDate = new Date(reservation.startTime);
    const endDate = new Date(reservation.endTime);

    const formattedDate = startDate.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
    });

    const formattedTime = `${startDate.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })} ~ ${endDate.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })}`;

    // 총 좌석 수
    const seatCount = seats.length;

    return (
        <Card className="w-full max-w-2xl mx-auto shadow-lg border-t-4 border-t-primary">
            <CardHeader className="bg-muted/20">
                <div className="flex items-center gap-2">
                    <CreditCardIcon className="h-6 w-6 text-primary" />
                    <CardTitle className="text-2xl">결제 확인</CardTitle>
                </div>
                <CardDescription>
                    선택하신 예매 내역을 최종 확인 후 결제를 진행해주세요.
                </CardDescription>
            </CardHeader>

            <CardContent className="space-y-6 pt-6">
                {/* 1. 영화 정보 섹션 */}
                <div className="flex flex-col gap-4 md:flex-row md:items-start">
                    {/* 포스터 영역 (실제 이미지 URL이 있다면 적용) */}
                    <div className="w-24 h-36 bg-gray-200 rounded-md flex-shrink-0 shadow-sm flex items-center justify-center text-muted-foreground text-xs">
                        {/* <img src={movie.posterUrl} alt={reservation.movieTitle} className="w-full h-full object-cover rounded-md" /> */}
                        NO POSTER
                    </div>
                    <div className="space-y-3 flex-1">
                        <div>
                            <div className="flex items-center gap-2 text-muted-foreground mb-1">
                                <FilmIcon className="h-4 w-4" />
                                <span className="text-sm font-medium">영화</span>
                            </div>
                            <h2 className="text-2xl font-bold leading-tight">{reservation.movieTitle}</h2>
                        </div>

                        {/* 상영관 및 일시 정보 */}
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                            <div className="flex items-center gap-2">
                                <MapPinIcon className="h-4 w-4 text-muted-foreground" />
                                <span>{reservation.theaterName}</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <CalendarIcon className="h-4 w-4 text-muted-foreground" />
                                <span>{formattedDate}</span>
                            </div>
                            <div className="flex items-center gap-2 md:col-span-2">
                                <ClockIcon className="h-4 w-4 text-muted-foreground" />
                                <span className="font-semibold">{formattedTime}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <Separator />

                {/* 2. 좌석 정보 섹션 */}
                <div>
                    <div className="flex items-center gap-2 mb-3">
                        <ArmchairIcon className="h-5 w-5 text-primary" />
                        <h3 className="font-semibold text-lg">선택 좌석 ({seatCount}석)</h3>
                    </div>
                    <div className="flex flex-wrap gap-2 p-4 bg-muted/30 rounded-lg border">
                        {seats.map((seat, idx) => (
                            // 행/열 번호를 보기 좋게 표시 (필요시 알파벳 변환 로직 추가 가능)
                            <Badge key={idx} variant="secondary" className="text-base px-3 py-1">
                                {seat.row}행 {seat.col}열
                            </Badge>
                        ))}
                    </div>
                </div>

                <Separator />

                {/* 3. 결제 금액 섹션 */}
                <div className="space-y-2">
                    {/* 티켓 금액 상세 (예시) */}
                    <div className="flex justify-between text-muted-foreground">
                        <span>일반 티켓 ({seatCount}매)</span>
                        <span>{(reservation.totalAmount).toLocaleString()}원</span>
                    </div>
                    {/* 할인 금액 등이 있다면 여기에 추가 */}

                    {/* 최종 결제 금액 */}
                    <div className="flex justify-between items-center mt-4 pt-4 border-t border-dashed">
                        <span className="font-bold text-xl">총 결제 금액</span>
                        <span className="font-extrabold text-3xl text-primary">
                            {reservation.totalAmount.toLocaleString()}<span className="text-xl font-normal ml-1">원</span>
                        </span>
                    </div>
                </div>
            </CardContent>

            {/* 4. 결제 버튼 (푸터) */}
            <CardFooter className="bg-muted/20 flex flex-col gap-3 pt-6">
                <Button
                    className="w-full text-lg h-12 font-bold"
                    size="lg"
                    onClick={() => {handlePayment(reservation)}}
                    disabled={paymentStatus.status === 'PENDING'}
                >
                    {paymentStatus.status === 'PENDING' ? (
                        <>
                            <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                            결제 처리 중...
                        </>
                    ) : (
                        `${reservation.totalAmount.toLocaleString()}원 결제하기`
                    )}
                </Button>
                <p className="text-xs text-center text-muted-foreground">
                    예매 변경 및 취소는 상영 시작 20분 전까지만 가능합니다.
                </p>
            </CardFooter>
        </Card>
    );
}