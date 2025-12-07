import {ReservationStatus} from "@/src/type/reservation/reservation";
import {Badge} from "@/components/ui/badge";
import {clsx} from "clsx";

type Props = {
    status: ReservationStatus;
    className?: string;
}

const LABELS: Record<ReservationStatus, string> = {
    PENDING: "예약 대기",
    CONFIRMED: "예약 확정",
    CANCELED: "취소됨",
    COMPLETED: "관람 완료",
};

const STYLES: Record<ReservationStatus, string> = {
    PENDING:
        "border-yellow-500/50 bg-yellow-50 text-yellow-700 hover:bg-yellow-100 dark:bg-yellow-900/30 dark:text-yellow-400",

    CONFIRMED:
        "border-green-500/50 bg-green-50 text-green-700 hover:bg-green-100 dark:bg-green-900/30 dark:text-green-400",

    CANCELED:
        "border-red-500/50 bg-red-50 text-red-700 hover:bg-red-100 dark:bg-red-900/30 dark:text-red-400",

    COMPLETED:
        "border-slate-500/50 bg-slate-50 text-slate-700 hover:bg-slate-100 dark:bg-slate-800/50 dark:text-slate-400",
}

export default function ReservationStatusBadge({status, className}: Readonly<Props>) {
    return (
        <Badge
            variant="outline"
            className={clsx("whitespace-nowrap transition-colors", STYLES[status], className)}
        >
            {LABELS[status]}
        </Badge>
    );
}