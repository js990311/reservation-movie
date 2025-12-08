import MovieInfo from "@/src/components/movie/movieInfo";
import ScreeningReservation from "@/src/components/screenings/ScreeningReservation";
import {getScreeningByIdAction} from "@/src/actions/screeningAction";

type Props = {
    params: Promise<{id: string}>
}

export default async function ScreeningIdPage({params}: Readonly<Props>){
    const {id} = await params;

    const screening = await getScreeningByIdAction(id);

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