import {Badge} from "@/components/ui/badge";
import {ArrowRight, CheckCircle2, Clock, CreditCard, Loader2, XCircle} from "lucide-react";
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
                {payments.map((payment) => (
                    <Card key={payment.paymentUid} className="overflow-hidden hover:shadow-md transition-all group">
                        <CardHeader className="flex flex-row items-start justify-between space-y-0 pb-2 bg-muted/10">
                            <div className="space-y-1">
                                <CardTitle className="text-base font-medium flex items-center gap-2">
                                    <span className="text-muted-foreground">결제 ID</span>
                                    <span className="font-mono text-sm bg-slate-100 px-2 py-0.5 rounded text-slate-700">
                                        {payment.paymentUid}
                                    </span>
                                </CardTitle>
                                <CardDescription>
                                    <p>
                                        관련 예매 번호: <span
                                        className="font-medium text-foreground">#{payment.reservationId}</span>
                                    </p>
                                    <p>
                                        결제 일시 : <span
                                        className="font-medium text-foreground">{new Date(payment.createAt).toLocaleString()}</span>
                                    </p>
                                </CardDescription>
                            </div>
                            <PaymentStatusBadge status={payment.paymentStatus} />
                        </CardHeader>

                        <CardContent className="pt-4 flex justify-between">
                            {/* 취소 정보가 있을 경우 노출 */}
                            {payment.cancelInfo && (
                                <div className="text-sm p-3 rounded-lg bg-red-50 dark:bg-red-900/10 border border-red-100 dark:border-red-900/20 text-red-600 dark:text-red-400">
                                    <p className="flex items-center gap-1.5 font-medium">
                                        <XCircle className="w-4 h-4" />
                                        취소 사유: {payment.cancelInfo.cancelReason || "정보 없음"}
                                    </p>
                                    <p className="text-xs mt-1 opacity-80">
                                        취소 일시: {new Date(payment.cancelInfo.canceledAt).toLocaleString()}
                                    </p>
                                </div>
                            )}
                            <Button variant="ghost" size="sm" className="gap-1 hover:text-primary hover:bg-primary/10" asChild>
                                <Link href={`/reservations/${payment.reservationId}`}>
                                    예매 상세 정보 확인
                                    <ArrowRight className="w-4 h-4 transition-transform group-hover:translate-x-1" />
                                </Link>
                            </Button>
                        </CardContent>
                    </Card>
                ))}
            </div>
        </div>
    );}