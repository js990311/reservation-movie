import {CreditCard, LogOut, Ticket, User} from "lucide-react";
import Link from "next/link";
import {Card, CardDescription, CardHeader, CardTitle} from "@/components/ui/card";

export default function MyPagePage(){

    const menuItems = [
        {
            title: "나의 예매 내역",
            description: "예매하신 영화 목록을 확인하고 관리합니다.",
            icon: <Ticket className="w-8 h-8 mb-2 text-primary" />,
            href: "/reservations/me",
            bgColor: "hover:bg-blue-50 dark:hover:bg-blue-900/20"
        },
        {
            title: "내 결제 내역",
            description: "결제된 영수증 및 환불 내역을 조회합니다.",
            icon: <CreditCard className="w-8 h-8 mb-2 text-green-600" />,
            href: "/payments/me", // 현재는 예매 내역과 동일하게 연결 (추후 /payments/history 등으로 분리 가능)
            bgColor: "hover:bg-green-50 dark:hover:bg-green-900/20"
        },
    ];

    return (
        <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
            <div className="flex items-center gap-4">
                <div className="w-16 h-16 rounded-full bg-slate-200 flex items-center justify-center">
                    <User className="w-8 h-8 text-slate-500"/>
                </div>
                <div>
                    <h1 className="text-2xl font-bold">반갑습니다, 회원님! 👋</h1>
                    <p className="text-muted-foreground">오늘도 즐거운 영화 관람 되세요.</p>
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {menuItems.map((item) => (
                    <Link key={item.title} href={item.href} className="block h-full">
                        <Card
                            className={`h-full transition-all duration-200 hover:shadow-md cursor-pointer border-2 hover:border-primary/20 ${item.bgColor}`}>
                            <CardHeader>
                                {item.icon}
                                <CardTitle>{item.title}</CardTitle>
                                <CardDescription>{item.description}</CardDescription>
                            </CardHeader>
                        </Card>
                    </Link>
                ))}
            </div>
        </div>)
}