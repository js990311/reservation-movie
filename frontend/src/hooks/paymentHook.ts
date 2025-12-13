import PortOne from "@portone/browser-sdk/v2";
import {useState} from "react";
import {ulid} from "ulid";
import {ReservationSummary} from "@/src/type/reservation/reservation";
import {paymentCompleteAction} from "@/src/actions/paymentAction";
import {PaymentLog} from "@/src/type/payment/paymentLog";

type PaymentStatus = {
    status: "IDLE" | "PENDING" | "FAILED" | "SUCCESS";
    message: string;
}

export default function usePayment(){
    const [paymentStatus, setPaymentStatus] = useState<PaymentStatus>({status: "IDLE", message: ""});
    const handlePayment = async (reservation: ReservationSummary)=>{
        setPaymentStatus({status: "PENDING", message: ""});
        const paymentId = ulid(); // 랜덤 payment Id 생성

        const storeId = process.env.NEXT_PUBLIC_PORTONE_STORE_ID;

        if(!storeId){
            setPaymentStatus({
                status: "FAILED",
                message: 'storeId 환경변수가 없습니다',
            });
            return;
        }

        const payment = await PortOne.requestPayment({
            // env 사용해서 key값 가져오기
            storeId: storeId,
            channelKey: process.env.NEXT_PUBLIC_PORTONE_CHANNEL_KEY,
            paymentId,
            orderName: `${reservation.movieTitle}_${reservation.theaterName}_${reservation.screeningId}`,
            totalAmount: reservation.totalAmount,
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
            });
            return;
        }
        else if (payment.code !== undefined) {
            setPaymentStatus({
                status: "FAILED",
                message: payment.message ?? 'Payment code를 이해할 수 없습니다.',
            })
            return;
        }

        const response = await paymentCompleteAction(paymentId);

        if (response.ok) {
            const paymentLog: PaymentLog = response.data;
            if(paymentLog.status === 'PAID'){
                setPaymentStatus({
                    status: "SUCCESS",
                    message: ""
                });
            }else {
                setPaymentStatus({
                    status: "FAILED",
                    message: "결제 검증 과정에서 실패했습니다"
                });
            }
        }else {
            setPaymentStatus({
                status: "FAILED",
                message: "결제 검증 과정에서 실패했습니다"
            });
        }
    }
    return {paymentStatus, handlePayment}
}
