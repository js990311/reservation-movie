import {ScreeningDetail} from "@/src/type/screening/screeningDetail";
import {serverFetch} from "@/src/lib/api/serverFetch";
import {logger} from "@/src/lib/logger/logger";

export async function getScreeningByIdAction(id: string){
    try {
        const response = await serverFetch<ScreeningDetail>({
            endpoint: `/screenings/${id}`,
            withAuth: true,
        });
        if(response.error) {
            logger.apiError(response.error);
        }
        return response.data;
    }catch (error) {
        console.log(error);
        return null;
    }
}
