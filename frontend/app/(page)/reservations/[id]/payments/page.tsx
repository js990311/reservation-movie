import PaymentSummaryCard from "@/src/components/reservation/paymentSummaryCard";
import {getReservationIdAction} from "@/src/actions/reservationAction";

type Props = {
    params: Promise<{id: string}>
}

export default async function PaymentsPage({params}: Readonly<Props>){
    const {id} = await params;
    const resrvationDetail = await getReservationIdAction(id);
    
    if(!resrvationDetail){
        return (
            <div>
                없습니다. 예약정보가
            </div>
        )
    }
    
    return (
        <div>
            <PaymentSummaryCard 
                reservationDetail={resrvationDetail}
            />
        </div>
    )
}