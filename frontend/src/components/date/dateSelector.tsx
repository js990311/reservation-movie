"use client"

import {useMemo} from "react";
import Link from "next/link";
import {Button} from "@/components/ui/button";
import {clsx} from "clsx";

interface Props {
    selectedDate: string;
    baseUrl: string;
}

export default function DateSelector({selectedDate, baseUrl}: Readonly<Props>) {
    const dates = useMemo(() => {
        const dates:{dateName:string, date:Date, dayOfWeek:number}[] = []
        for (let i = -2; i < 7; i++) {
            const d = new Date(selectedDate);
            d.setDate(d.getDate() + i)
            dates.push({
                dateName : d.toISOString().split("T")[0],
                date: d,
                dayOfWeek : d.getDay()
            });
        }
        return dates
    }, [selectedDate]);

    return (
        <div>
            <div className={"flex overflow-scroll scrollbar-hidden"}>
                {
                    dates.map((date, index) => (
                        <Link key={date.dateName} href={`${baseUrl}?date=${date.dateName}`}>
                            <Button
                                className={clsx(
                                    "text-xl cursor-pointer flex flex-col h-auto py-3 px-4 min-w-[7rem] gap-1 transition-all bg-transparent hover:text-white",
                                    {'text-red-600 hover:bg-red-600' : date.dayOfWeek === 0},
                                    {'text-blue-600 hover:bg-blue-600' : date.dayOfWeek === 6},
                                    {'text-black-600 hover:bg-gray-600' : date.dayOfWeek !== 0 && date.dayOfWeek !== 6},
                                )}
                            >
                                {date.dateName}
                            </Button>
                        </Link>
                    ))
                }
            </div>
        </div>
    );
}