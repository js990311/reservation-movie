import PortOne from "@portone/browser-sdk/v2";
import {useState} from "react";
import {ulid} from "ulid";
import {ReservationSummary} from "@/src/type/reservation/reservation";

type PaymentStatus = {
    status: "IDLE" | "PENDING" | "FAILED" | "SUCCESS";
    message: string;
}

export default function usePayment(){
    const [paymentStatus, setPaymentStatus] = useState<PaymentStatus>({status: "IDLE", message: ""});
    const handlePayment = async (reservation: ReservationSummary)=>{
        setPaymentStatus({status: "PENDING", message: ""});
        const paymentId = ulid(); // 랜덤 payment Id 생성

        const payment = await PortOne.requestPayment({
            // env 사용해서 key값 가져오기
            storeId: process.env.NEXT_PUBLIC_PORTONE_STORE_ID,
            channelKey: process.env.NEXT_PUBLIC_PORTONE_CHANNEL_KEY,
            paymentId,
            orderName: `${reservation.movieTitle}_${reservation.theaterName}_${reservation.screeningId}`,
            totalAmount: `${reservation.totalAmount}`,
            currency: "KRW",
            payMethod: "EASY_PAY",
            customData: {
                reservationId: reservation.reservationId
            }
        });

        if(!payment){
            setPaymentStatus({
                status: "FAILED",
                message: 'payment is not exists',
            })
        }
        else if (payment.code !== undefined) {
            setPaymentStatus({
                status: "FAILED",
                message: payment.message,
            })
            return;
        }else {
            setPaymentStatus({
                status: "SUCCESS",
                message: payment.message,
            })
        }
        // TODO: payment 완료시 서버로 쿼리 보내기
    }
    return {paymentStatus, handlePayment}
}
