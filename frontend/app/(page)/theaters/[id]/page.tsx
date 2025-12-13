import SeatMap from "@/src/components/theater/seatMap";
import {Accordion, AccordionContent, AccordionItem, AccordionTrigger} from "@/components/ui/accordion";
import DateSelector from "@/src/components/date/dateSelector";
import TheaterScreeningList from "@/src/components/theater/theaterScreeningList";
import {getTheaterByIdAction} from "@/src/actions/theaterAction";

interface Props {
    params : Promise<{id: string}>;
    searchParams: Promise<{date: string}>;
}

export default async function TheaterIdPage({params, searchParams} : Readonly<Props>){
    const {id} = await params;
    const {date} = await searchParams;
    const selectedDate = date ?? new Date().toISOString().split("T")[0];
    const response = await getTheaterByIdAction(id);

    if(!response.ok) {
        return (
            <div>
                404 상영관 정보가 없습니다.
            </div>
        )
    }

    const theater = response.data;

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
                        <SeatMap seats={theater.seats} rowSize={theater.rowSize} colSize={theater.colSize}></SeatMap>
                    </AccordionContent>
                </AccordionItem>
                <AccordionItem value={"reservation"}>
                    <AccordionTrigger>
                        상영하는 영화보기
                    </AccordionTrigger>
                    <AccordionContent>
                        <DateSelector
                            selectedDate={selectedDate}
                            baseUrl={`/theaters/${id}`}
                        />
                        <TheaterScreeningList theaterId={id} selectedDate={selectedDate}/>
                    </AccordionContent>
                </AccordionItem>

            </Accordion>
        </div>
    );
}