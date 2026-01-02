import {Badge} from "@/components/ui/badge";
import {ArrowRight, CheckCircle2, Clock, CreditCard, Loader2, RotateCcw, XCircle} from "lucide-react";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
import Link from "next/link";
import {clsx} from "clsx";
import {PaymentInfo} from "@/src/type/payment/paymentInfo";
import {PaymentStatus} from "@/src/type/payment/paymentStatus";

type Props = {
    payments: PaymentInfo[]
}

function PaymentStatusBadge({ status }: { status: PaymentStatus }) {
    const config = {
        READY: {
            label: "결제 대기",
            icon: <CreditCard className="w-3.5 h-3.5" />,
            style: "border-slate-200 bg-slate-50 text-slate-700 dark:bg-slate-800 dark:text-slate-400"
        },
        VERIFYING: {
            label: "검증 중",
            icon: <Loader2 className="w-3.5 h-3.5 animate-spin" />,
            style: "border-blue-200 bg-blue-50 text-blue-700 dark:bg-blue-900/20 dark:text-blue-400"
        },
        PAID: {
            label: "결제 완료",
            icon: <CheckCircle2 className="w-3.5 h-3.5" />,
            style: "border-green-200 bg-green-50 text-green-700 dark:bg-green-900/20 dark:text-green-400"
        },
        ABORTED: {
            label: "결제 중단",
            icon: <XCircle className="w-3.5 h-3.5" />,
            style: "border-red-200 bg-red-50 text-red-700 dark:bg-red-900/20 dark:text-red-400"
        },
        TIMEOUT: {
            label: "시간 초과",
            icon: <Clock className="w-3.5 h-3.5" />,
            style: "border-orange-200 bg-orange-50 text-orange-700 dark:bg-orange-900/20 dark:text-orange-400"
        }
    };

    // 정의되지 않은 상태가 올 경우 기본값 처리
    const currentState = config[status as keyof typeof config] || config.READY;

    return (
        <Badge
            variant="outline"
            className={clsx("gap-1.5 py-1 px-3", currentState.style)}
        >
            {currentState.icon}
            {currentState.label}
        </Badge>
    );
}

export default function MyPayments({payments}: Readonly<Props>) {

    if (!payments || payments.length === 0) {
        return (
            <div className="flex flex-col items-center justify-center py-16 border-2 border-dashed rounded-xl bg-slate-50 text-muted-foreground animate-in fade-in zoom-in-95 duration-500">
                <CreditCard className="w-12 h-12 mb-3 opacity-20" />
                <p className="font-medium text-lg">결제 내역이 없습니다.</p>
            </div>
        );
    }

    return (
        <div className="space-y-6 animate-in fade-in slide-in-from-bottom-2 duration-500">
            <div className="flex items-center justify-between">
                <h2 className="text-2xl font-bold tracking-tight">내 결제 내역</h2>
                <Badge variant="secondary" className="px-3 py-1">
                    총 {payments.length}건
                </Badge>
            </div>

            <div className="grid gap-4">
                {payments.map((payment) => {
                    const isCancelled = !!(payment.cancelInfo && payment.cancelInfo.cancelReason);
                    return (
                    <Card key={payment.paymentUid}
                          className={clsx(
                              "relative overflow-hidden transition-all duration-300",
                              isCancelled ? "opacity-80 bg-slate-50/50 border-red-100" : "hover:border-primary/50 hover:shadow-lg"
                          )}
                    >
                        {isCancelled && <div className="absolute top-0 left-0 w-full h-1 bg-red-400" />}
                        <CardHeader className="pb-4">
                            <div className="flex items-center justify-between">
                                <div className="space-y-1">
                                    <div className="flex items-center gap-3">
                                            <span className="font-mono text-sm font-bold text-primary">
                                                {payment.paymentUid}
                                            </span>
                                        <PaymentStatusBadge status={payment.paymentStatus} />
                                    </div>
                                    <CardDescription className="flex items-center gap-2 mt-1">
                                        예매 번호 <span className="text-foreground font-semibold">#{payment.reservationId}</span>
                                        <span className="text-slate-300">|</span>
                                        <span>{new Date(payment.createAt).toLocaleString()}</span>
                                    </CardDescription>
                                </div>
                            </div>
                        </CardHeader>

                        <CardContent className="pt-0 space-y-4">
                            {isCancelled ? (
                                <div className="flex items-start gap-3 p-4 rounded-xl bg-red-50 border border-red-100 text-red-800">
                                    <RotateCcw className="w-5 h-5 mt-0.5 shrink-0 text-red-500" />
                                    <div className="space-y-1">
                                        <p className="text-sm font-bold">환불 처리가 완료된 결제입니다.</p>
                                        <p className="text-sm opacity-90">사유: {payment.cancelInfo?.cancelReason}</p>
                                        <p className="text-[11px] font-medium opacity-70 uppercase tracking-wider">
                                            취소 일시: {new Date(payment.cancelInfo!.canceledAt).toLocaleString()}
                                        </p>
                                    </div>
                                </div>
                            ) : (
                                <div className="h-[1px] bg-slate-100 w-full" />
                            )}

                            <div className="flex justify-end items-center gap-3">
                                <Button variant="outline" size="sm" className="rounded-full px-4 text-xs font-semibold" asChild>
                                    <Link href={`/reservations/${payment.reservationId}`}>
                                        상세 보기
                                    </Link>
                                </Button>
                            </div>
                        </CardContent>
                    </Card>
                    )})}
            </div>
        </div>
    );}