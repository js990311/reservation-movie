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
    return (
        <div>
            <ReservationList
                reservations={response.data}
            />
            <PaginationRemote pagination={response.pagination}/>
        </div>
    )
}