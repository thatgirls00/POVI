'use client'

import { useEffect } from 'react'
import { useRouter } from 'next/navigation'

export default function OAuth2RedirectPage() {
    const router = useRouter()

    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search)
        const accessToken = urlParams.get('accessToken')
        const refreshToken = urlParams.get('refreshToken')

        if (accessToken && refreshToken) {
            // ✅ 토큰을 localStorage에 저장
            localStorage.setItem('accessToken', accessToken)
            localStorage.setItem('refreshToken', refreshToken)

            alert('소셜 로그인 성공! 🎉')
            router.push('/') // 홈으로 리다이렉트
        } else {
            alert('토큰 정보가 없습니다.')
            router.push('/login')
        }
    }, [router])

    return (
        <div className="flex justify-center items-center min-h-screen">
            <p>로그인 처리 중입니다...</p>
        </div>
    )
}