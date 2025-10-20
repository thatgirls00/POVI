'use client'

import { useEffect, useState } from "react"
import { useSearchParams } from "next/navigation"
import axios from "axios"
import { CheckCircle2, XCircle, Loader2 } from "lucide-react"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import Link from "next/link"

export default function EmailVerificationPage() {
    const searchParams = useSearchParams()
    const [status, setStatus] = useState<"loading" | "success" | "fail">("loading")

    useEffect(() => {
        if (typeof window === "undefined") return

        const token = searchParams.get("token")?.trim()
        if (!token) {
            setStatus("fail")
            return
        }

        const verifyEmail = async () => {
            const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080"
            const url = `${baseUrl}/auth/email/verify`

            try {
                const res = await axios.get(url, {
                    params: { token },
                    headers: { "Accept": "application/json" },
                    withCredentials: true
                })

                if (res.status === 200) {
                    setStatus("success")
                } else {
                    setStatus("fail")
                }
            } catch {
                setStatus("fail")
            }
        }

        verifyEmail()
    }, [searchParams])

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-indigo-100 via-white to-pink-100 px-4">
            <Card className="w-full max-w-md text-center p-6 shadow-xl border border-gray-200 rounded-2xl">
                <CardHeader>
                    <CardTitle className="text-2xl font-bold tracking-tight text-gray-800">
                        이메일 인증
                    </CardTitle>
                </CardHeader>

                <CardContent className="flex flex-col items-center justify-center space-y-5">
                    {status === "loading" && (
                        <>
                            <Loader2 className="animate-spin h-12 w-12 text-blue-500" />
                            <p className="text-base text-gray-600">이메일 인증 중입니다...</p>
                        </>
                    )}

                    {status === "success" && (
                        <>
                            <CheckCircle2 className="h-14 w-14 text-green-500" />
                            <p className="text-lg font-semibold text-gray-800">이메일 인증 완료</p>
                            <p className="text-sm text-gray-600">
                                인증이 성공적으로 완료되었습니다. <br />
                                <span className="font-medium text-black">회원가입 페이지에서 인증 확인 버튼</span>을 눌러주세요.
                            </p>
                            <Link
                                href="/signup"
                                className="mt-2 inline-block px-4 py-2 rounded-lg bg-blue-500 text-white text-sm font-medium hover:bg-blue-600 transition-colors"
                            >
                                회원가입 페이지로 돌아가기
                            </Link>
                        </>
                    )}

                    {status === "fail" && (
                        <>
                            <XCircle className="h-12 w-12 text-red-500" />
                            <p className="text-lg font-semibold text-gray-800">유효하지 않은 링크</p>
                            <p className="text-sm text-gray-600">
                                만료되었거나 잘못된 인증 링크입니다. <br />
                                다시 시도해주세요.
                            </p>
                            <Link
                                href="/signup"
                                className="mt-2 inline-block px-4 py-2 rounded-lg bg-blue-500 text-white text-sm font-medium hover:bg-blue-600 transition-colors"
                            >
                                회원가입 페이지로 돌아가기
                            </Link>
                        </>
                    )}
                </CardContent>
            </Card>
        </div>
    )
}