"use client"

import {Button} from "@/components/ui/button";
import {useRouter} from "next/navigation";
import {reservationCancelAction} from "@/src/actions/reservationAction";
import toast from "react-hot-toast";
import {logger} from "@/src/lib/logger/logger";

type Props = {
    reservationId: number;
}

export default function ReservationCancelButton({reservationId}: Readonly<Props>) {
    const router = useRouter();

    const handleCancel = async () => {
        const response = await reservationCancelAction(reservationId);
        if(response.ok){
            toast.success("성공적으로 취소했습니다.");
            router.refresh();
        }else {
            logger.apiError(response.error);
            toast.error(`취소에 실패헀습니다.`);
        }
    }

    return (
        <Button className={"cursor-pointer text-red-600 bg-white border-red-600 border hover:bg-red-700 hover:text-white"} onClick={handleCancel}>
            취소하기
        </Button>
    )
}