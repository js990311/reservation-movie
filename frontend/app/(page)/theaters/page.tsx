import {Theater} from "@/src/type/theater/theater";
import TheaterCard from "@/src/components/screenings/theaterCard";
import {getTheatersAction} from "@/src/actions/theaterAction";
import {BaseResponse} from "@/src/type/response/base";


export default async function TheaterPage(){
    const {data: theaters, pagination}: BaseResponse<Theater[]> = await getTheatersAction();

    if(!theaters){
        return (
            <div>
                영화관이 없습니다
            </div>
        )
    }
    
    return (
        <div>
            {
                theaters.length === 0 ? (
                    <div>
                        영화관 정보를 불러올 수 없습니다
                    </div>
                ) : (
                    theaters.map((theater: Theater) => (
                        <TheaterCard
                            key={theater.theaterId}
                            theater={theater}
                        ></TheaterCard>
                    ))
                )
            }
        </div>
    );
}
