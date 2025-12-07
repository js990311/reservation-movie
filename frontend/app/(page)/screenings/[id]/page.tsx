import MovieInfo from "@/src/components/movie/movieInfo";
import {ProxyRequestBuilder} from "@/src/lib/api/proxyRequestBuilder";
import {BaseResponse} from "@/src/type/response/base";
import {ScreeningDetail} from "@/src/type/screening/screeningDetail";
import SeatMap from "@/src/components/theater/seatMap";
import ScreeningReservation from "@/src/components/screenings/ScreeningReservation";

type Props = {
    params: Promise<{id: string}>
}

async function getScreeningById(id: string){
    try {
        const response = await new ProxyRequestBuilder(`/screenings/${id}`).withMethod("GET").withAuth().execute();
        if(!response.ok) {
            const error = await response.text();
            console.log(`response가 정상이 아님 ${response.status}`);
            return null;
        }
        const respData: BaseResponse<ScreeningDetail> = await response.json();
        return respData.data;
    }catch (error) {
        console.log(error);
        return null;
    }
}

export default async function ScreeningIdPage({params}: Readonly<Props>){
    const {id} = await params;

    const screening = await getScreeningById(id);

    if(!screening){
        return (
            <div>
                상영표 정보가 없습니다
            </div>
        )
    }
    
    return (
        <div>
            <MovieInfo movie={screening.movie}/>
            <ScreeningReservation
                screeningId={screening.screening.screeningId}
                seats={screening.seats}
                theater={screening.theater}
            />
        </div>
    );
}