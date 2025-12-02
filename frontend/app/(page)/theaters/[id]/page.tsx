import {ProxyRequestBuilder} from "@/src/lib/api/proxyRequestBuilder";
import {PaginationResponse} from "@/src/type/response/pagination";
import {Theater} from "@/src/type/theater/theater";
import {BaseResponse} from "@/src/type/response/base";
import {Screening} from "@/src/type/screening/screening";
import SeatMap from "@/src/components/theater/seatMap";
import {Accordion, AccordionContent, AccordionItem, AccordionTrigger} from "@/components/ui/accordion";

async function getTheaterById(id: string) {
    try {
        const response = await new ProxyRequestBuilder(`/theaters/${id}`).withMethod("GET").execute();
        if(!response.ok) {
            return null;
        }
        const respData: BaseResponse<Theater> = await response.json();
        return respData.data;
    }catch (error) {
        return null;
    }
}

async function getTheaterScreening(id: string) {
    try {
        const response = await new ProxyRequestBuilder(`/theaters/${id}/screenings`).withMethod("GET").execute();
        if(!response.ok) {
            return [];
        }
        const respData: PaginationResponse<Screening> = await response.json();
        return respData.data;
    }catch (error) {
        return [];
    }
}

export default async function TheaterIdPage({params} : {params : Promise<{id: string}>}){
    const {id} = await params;
    const theater = await getTheaterById(id);

    if(!theater) {
        return (
            <div>
                404 상영관 정보가 없습니다.
            </div>
        )
    }

    return (
        <div className="container mx-auto py-10 px-4 space-y-10">
            <div className="border-b pb-4">
                <h1 className="text-3xl font-bold">{theater.name}</h1>
                <p className="text-muted-foreground">총 {theater.seats.length}석</p>
            </div>

            <Accordion type={"single"} collapsible>
                <AccordionItem value={"seatMap"}>
                    <AccordionTrigger>
                        좌석 보기
                    </AccordionTrigger>
                    <AccordionContent>
                        <SeatMap seats={theater.seats}></SeatMap>
                    </AccordionContent>
                </AccordionItem>
                <AccordionItem value={"reservation"}>
                    <AccordionTrigger>
                        상영하는 영화보기
                    </AccordionTrigger>
                    <AccordionContent>
                    </AccordionContent>
                </AccordionItem>

            </Accordion>
        </div>
    );
}