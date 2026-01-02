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
    const response = await getPaymentsAction(page ?? 0, size ?? 10);

    if(!response.ok){
        return (
            <div>
                예외가 발생했습니다.
            </div>
        )
    }

    const {data: payments, pagination} = response;

    return (
        <div>
            <MyPayments payments={payments}></MyPayments>
            <PaginationRemote baseUrl={'/payments/me'} pagination={pagination}/>
        </div>
    );
}