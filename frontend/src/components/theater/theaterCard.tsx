import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Armchair, MapPin} from "lucide-react";
import {Badge} from "@/components/ui/badge";
import {Theater} from "@/src/type/theater/theater";
import Link from "next/link";

type TheaterCardProps = {
    theater: Theater;
}

export default function TheaterCard({theater}: Readonly<TheaterCardProps>) {
    return (
        <Card
            className={`
                transition-all duration-200 hover:shadow-md border-2 border-transparent hover:border-border
            `}
        >
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-lg font-bold flex items-center gap-2">
                    <MapPin className="h-5 w-5 text-primary" />
                    <Link href={`/theaters/${theater.theaterId}`} className={"cursor-pointer hover:underline"}>
                        {theater.name}
                    </Link>
                </CardTitle>
                {/* 선택 상태 표시 (체크 아이콘 등) 가능 */}
            </CardHeader>
            <CardContent>
                <div className="flex items-center justify-between mt-2">
                    <div className="flex items-center text-sm text-muted-foreground">
                        <Armchair className="mr-1 h-4 w-4" />
                    </div>
                    {/* 뱃지로 강조 */}
                    <Badge variant="secondary">
                        OPEN
                    </Badge>
                </div>
            </CardContent>
        </Card>
    );
}