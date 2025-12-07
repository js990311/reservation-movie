import PaymentSummaryCard from "@/src/components/reservation/paymentSummaryCard";
import {getMyReservationsAction} from "@/src/actions/reservationAction";
import {ProxyRequestBuilder} from "@/src/lib/api/proxyRequestBuilder";
import {BaseResponse} from "@/src/type/response/base";
import {ReservationDetail} from "@/src/type/reservation/reservation";

type Props = {
    params: Promise<{id: string}>
}

async function getReservationId(id: string){
    try {
        const response = await new ProxyRequestBuilder(`/reservations/${id}`)
            .withMethod('GET')
            .withAuth()
            .execute();
        if(response.ok){
            const data:BaseResponse<ReservationDetail> = await response.json();
            return data.data;
        }else {
            return null;
        }
    }catch (error){
        return null;
    }
}

export default async function PaymentsPage({params}: Readonly<Props>){
    const {id} = await params;
    const resrvationDetail = await getReservationId(id);
    
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