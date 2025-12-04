"use client"

import {Button} from "@/components/ui/button";
import {ChevronLeft, ChevronRight} from "lucide-react";
import {PaginationMetadata} from "@/src/type/response/pagination";
import {useRouter} from "next/navigation";

type Props = {
    pagination: PaginationMetadata;
    baseUrl: string
}

export default function PaginationRemote({pagination, baseUrl}: Readonly<Props>) {
    const router = useRouter();

    const onPageMove = (page: number) => {
        router.push(`${baseUrl}?page=${page}`);
    }

    return (
        <div>
            <div className="flex justify-center gap-2 mt-8">
                <Button
                    variant="outline"
                    disabled={pagination.requestNumber === 1}
                    onClick={() => {
                        onPageMove(pagination.requestNumber-1);
                    }}
                >
                    <ChevronLeft/>
                    이전
                </Button>
                <span className="flex items-center px-4 text-sm font-medium">
                    {pagination.requestNumber} / {pagination.totalPage}
                </span>
                <Button
                    variant="outline"
                    disabled = {!pagination.hasNextPage}
                    onClick={() => {
                        onPageMove(pagination.requestNumber+1);
                    }}
                >
                    다음
                    <ChevronRight/>
                </Button>
            </div>
        </div>
    )
}