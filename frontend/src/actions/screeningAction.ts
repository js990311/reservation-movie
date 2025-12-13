import {ScreeningDetail} from "@/src/type/screening/screeningDetail";
import {serverFetch} from "@/src/lib/api/serverFetch";
import {logger} from "@/src/lib/logger/logger";
import {fetchOne} from "@/src/lib/api/fetchWrapper";
import {BaseError, unknownFetchException} from "@/src/lib/api/error/apiErrors";
import {failResult, oneResult} from "@/src/type/response/result";

export async function getScreeningByIdAction(id: string){
    try {
        const response = await fetchOne<ScreeningDetail>({
            endpoint: `/screenings/${id}`,
            withAuth: true,
        });
        return oneResult(response);
    }catch (error) {
        if(error instanceof BaseError){
            return failResult(error.details);
        }else {
            if(error instanceof BaseError){
                return failResult(error.details);
            }else {
                const exception = unknownFetchException('getScreeningByIdAction',error);
                return failResult(exception.details);
            }
        }
    }
}
