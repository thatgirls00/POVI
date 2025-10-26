import type React from "react"
import type { Metadata } from "next"
import { GeistSans } from "geist/font/sans"
import { GeistMono } from "geist/font/mono"
import { Analytics } from "@vercel/analytics/next"
import { Suspense } from "react"
import "./globals.css"

export const metadata: Metadata = {
    title: "POVI - 감정 공유 다이어리",
    description: "감정을 기록하고 위로를 주고받는 감정 공유형 다이어리 서비스",
    generator: "v0.app",
}

export default function RootLayout({
                                       children,
                                   }: Readonly<{
    children: React.ReactNode
}>) {
    return (
        <html lang="ko">
        <body
            className={`font-sans ${GeistSans.variable} ${GeistMono.variable} antialiased bg-[#fffaf5] text-gray-900`}
        >
        <div className="flex justify-center">
            <main className="w-full max-w-5xl px-4 sm:px-6 md:px-8">
                <Suspense fallback={null}>{children}</Suspense>
            </main>
        </div>
        <Analytics />
        </body>
        </html>
    )
}
