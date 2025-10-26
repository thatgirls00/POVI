'use client'

import { useState } from 'react'
import api from '@/lib/axios' // ✅ axios 기본 설정 파일 (lib/axios.ts)
import Link from 'next/link'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Header } from '@/components/header'
import { Heart } from 'lucide-react'

export default function LoginPage() {
  // ✅ 상태값 관리
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)

  // ✅ 로그인 요청 함수
  const handleLogin = async () => {
    if (!email || !password) {
      alert('이메일과 비밀번호를 입력해주세요.')
      return
    }

    try {
      setLoading(true)

      const response = await api.post('/auth/login', { email, password })
      const { accessToken, refreshToken } = response.data

      localStorage.setItem('accessToken', accessToken)
      localStorage.setItem('refreshToken', refreshToken)

      alert('로그인 성공! 🎉')
      window.location.href = '/'
    } catch (error: any) {
      console.error('❌ 로그인 실패:', error)

      const status = error.response?.status
      const message = error.response?.data?.message

      if (status === 400 && message === '입력값 검증 실패') {
        alert('입력한 이메일 또는 비밀번호가 형식에 맞지 않습니다.')
      } else if (status === 401 || status === 403) {
        // 이메일 인증 안됨 or 권한 없음
        alert(message || '로그인 권한이 없습니다.')
      } else {
        alert('로그인 실패: ' + (message || '서버 오류'))
      }
    } finally {
      setLoading(false)
    }
  }

  return (
      <div className="min-h-screen">
        <Header />

        <main className="container flex items-center justify-center py-12 md:py-20">
          <Card className="w-full max-w-md">
            <CardHeader className="space-y-1 text-center">
              <div className="flex justify-center mb-4">
                <Heart className="h-12 w-12 text-primary fill-primary" />
              </div>
              <CardTitle className="text-2xl">로그인</CardTitle>
              <CardDescription>POVI에 오신 것을 환영합니다</CardDescription>
            </CardHeader>

            <CardContent className="space-y-4">
              <div className="space-y-3">
                {/* ✅ 카카오 로그인 버튼 */}
                <Button
                    asChild
                    variant="outline"
                    className="w-full gap-2 bg-[#FEE500] hover:bg-[#FEE500]/90 text-[#000000] border-[#FEE500]"
                >
                  <a href="http://localhost:8080/oauth2/authorization/kakao">
                    <svg className="h-5 w-5" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M12 3c-4.97 0-9 3.582-9 8 0 1.988.78 3.82 2.084 5.267L3 21l5.26-1.867C9.462 19.69 10.693 20 12 20c4.97 0 9-3.582 9-8s-4.03-8-9-8z" />
                    </svg>
                    카카오로 시작하기
                  </a>
                </Button>

                {/* ✅ 구글 로그인 버튼 */}
                <Button
                    variant="outline"
                    className="w-full gap-2 bg-transparent"
                    onClick={() =>
                        (window.location.href = `${process.env.NEXT_PUBLIC_API_BASE_URL}/oauth2/authorization/google`)
                    }
                >
                  <svg className="h-5 w-5" viewBox="0 0 24 24">
                    <path
                        fill="#4285F4"
                        d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                    />
                    <path
                        fill="#34A853"
                        d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                    />
                    <path
                        fill="#FBBC05"
                        d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                    />
                    <path
                        fill="#EA4335"
                        d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                    />
                  </svg>
                  Google로 시작하기
                </Button>
              </div>

              {/* 구분선 */}
              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <span className="w-full border-t" />
                </div>
                <div className="relative flex justify-center text-xs uppercase">
                  <span className="bg-background px-2 text-muted-foreground">또는</span>
                </div>
              </div>

              {/* ✅ 이메일 입력 */}
              <div className="space-y-2">
                <Label htmlFor="email">이메일</Label>
                <Input
                    id="email"
                    type="email"
                    placeholder="your@email.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
              </div>

              {/* ✅ 비밀번호 입력 */}
              <div className="space-y-2">
                <Label htmlFor="password">비밀번호</Label>
                <Input
                    id="password"
                    type="password"
                    placeholder="••••••••"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
              </div>

              {/* ✅ 로그인 버튼 */}
              <Button className="w-full" onClick={handleLogin} disabled={loading}>
                {loading ? '로그인 중...' : '로그인'}
              </Button>
            </CardContent>

            <CardFooter className="flex flex-col gap-4">
              <div className="text-sm text-center text-muted-foreground">
                계정이 없으신가요?{' '}
                <Link href="/signup" className="text-primary hover:underline">
                  회원가입
                </Link>
              </div>
            </CardFooter>
          </Card>
        </main>
      </div>
  )
}