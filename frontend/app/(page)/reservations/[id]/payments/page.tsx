import PaymentSummaryCard from "@/src/components/reservation/paymentSummaryCard";
import {getReservationIdAction} from "@/src/actions/reservationAction";

type Props = {
    params: Promise<{id: string}>
}

export default async function PaymentsPage({params}: Readonly<Props>){
    const {id} = await params;
    const response = await getReservationIdAction(id);


    if(!response.ok){
        return (
            <div>
                없습니다. 예약정보가
            </div>
        )
    }

    const reservationDetail = response.data;
    
    return (
        <div>
            <PaymentSummaryCard 
                reservationDetail={reservationDetail}
            />
        </div>
    )
}