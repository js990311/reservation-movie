import {ProxyRequestBuilder} from "@/src/lib/api/proxyRequestBuilder";
import {PaginationResponse} from "@/src/type/response/pagination";
import {Theater} from "@/src/type/theater/theater";
import TheaterCard from "@/src/components/screenings/theaterCard";

async function getTheaters(){
    try {
        const response = await new ProxyRequestBuilder('/theaters').withMethod("GET").execute();
        if(!response.ok) {
            return [];
        }
        const respData: PaginationResponse<Theater> = await response.json();
        return respData.data;
    }catch (error) {
        return [];
    }
}

export default async function TheaterPage(){
    const theaters: Theater[] = await getTheaters();

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
