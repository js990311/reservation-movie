import {getMyReservationsAction} from "@/src/actions/reservationAction";
import ReservationList from "@/src/components/reservation/reservationList";
import PaginationRemote from "@/src/components/pagination/paginationRemote";

type Props = {
    searchParams: Promise<{
        page: number;
        size: number;
    }>;
}

export default async function ReservationMePage({searchParams}: Readonly<Props>){
    const {page, size} = await searchParams;
    const response = await getMyReservationsAction(page ?? 0, size ?? 10);

    if(!response.ok){
        return (
            <div>
                예외가 발생했습니다.
            </div>
        )
    }

    const {data: reservations, pagination} = response;

    return (
        <div>
            <ReservationList
                reservations={reservations}
            />
            <PaginationRemote pagination={pagination}/>
        </div>
    )
}