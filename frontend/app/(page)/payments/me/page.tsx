import PaginationRemote from "@/src/components/pagination/paginationRemote";
import {getPaymentsAction} from "@/src/actions/paymentAction";
import {BaseResponse} from "@/src/type/response/base";
import {PaymentLog} from "@/src/type/payment/paymentLog";
import MyPayments from "@/src/components/payments/myPayments";

type Props = {
    searchParams: Promise<{
        page: number;
        size: number;
    }>;
}

export default async function PaymentsMePage({searchParams}: Readonly<Props>) {
    const {page=0, size=10} = await searchParams;
    const {data, pagination}:BaseResponse<PaymentLog[]> = await getPaymentsAction(page ?? 0, size ?? 10);

    return (
        <div>
            <MyPayments payments={data}></MyPayments>
            <PaginationRemote pagination={pagination}/>
        </div>
    );
}