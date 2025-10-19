"use client"

import Link from "next/link"
import { useState } from "react"
import { useRouter } from "next/navigation"
import axios from "axios"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Header } from "@/components/header"
import { Heart } from "lucide-react"

export default function SignupPage() {
  const [form, setForm] = useState({
    email: "",
    password: "",
    confirmPassword: "",
    nickname: "",
  })

  const [isEmailVerified, setIsEmailVerified] = useState(false)
  const [emailSent, setEmailSent] = useState(false)

  const router = useRouter()

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.id]: e.target.value })
    // 이메일 변경 시 인증 상태 초기화
    if (e.target.id === "email") {
      setIsEmailVerified(false)
      setEmailSent(false)
    }
  }

  // ✉️ 이메일 인증 요청
  const handleSendEmailVerification = async () => {
    try {
      await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/auth/email/send`, {
        email: form.email,
      })
      alert("인증 이메일이 전송되었습니다. 메일을 확인하세요.")
      setEmailSent(true)
    } catch (err: any) {
      alert(err.response?.data?.message || "이메일 전송에 실패했습니다.")
    }
  }

  // ✅ 이메일 인증 확인
  const handleCheckEmailVerified = async () => {
    try {
      const response = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/auth/email/status`, {
        params: { email: form.email },
      })

      if (response.data.verified) {
        alert("이메일 인증이 확인되었습니다.")
        setIsEmailVerified(true)
      } else {
        alert("아직 인증되지 않았습니다. 이메일을 확인해주세요.")
      }
    } catch (err: any) {
      alert(err.response?.data?.message || "이메일 인증 확인에 실패했습니다.")
    }
  }

  // 📝 회원가입
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (form.password !== form.confirmPassword) {
      alert("비밀번호가 일치하지 않습니다.")
      return
    }

    if (!isEmailVerified) {
      alert("이메일 인증을 완료해주세요.")
      return
    }

    try {
      await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/auth/signup`, {
        email: form.email,
        password: form.password,
        nickname: form.nickname,
        provider: "LOCAL",
        providerId: "none",
      })

      alert("회원가입이 완료되었습니다.")
      router.push("/login")
    } catch (err: any) {
      alert(err.response?.data?.message || "회원가입에 실패했습니다.")
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
              <CardTitle className="text-2xl">회원가입</CardTitle>
              <CardDescription>감정을 기록하고 공유할 준비가 되셨나요?</CardDescription>
            </CardHeader>

            <CardContent className="space-y-4">
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="nickname">닉네임</Label>
                  <Input
                      id="nickname"
                      placeholder="사용할 닉네임을 입력하세요"
                      value={form.nickname}
                      onChange={handleChange}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="email">이메일</Label>
                  <Input
                      id="email"
                      type="email"
                      placeholder="your@email.com"
                      value={form.email}
                      onChange={handleChange}
                  />
                  <div className="flex gap-2 mt-2">
                    <Button type="button" variant="outline" onClick={handleSendEmailVerification}>
                      인증 메일 발송
                    </Button>
                    <Button
                        type="button"
                        variant="secondary"
                        disabled={!emailSent}
                        onClick={handleCheckEmailVerified}
                    >
                      인증 확인
                    </Button>
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="password">비밀번호</Label>
                  <Input
                      id="password"
                      type="password"
                      placeholder="••••••••"
                      value={form.password}
                      onChange={handleChange}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="confirmPassword">비밀번호 확인</Label>
                  <Input
                      id="confirmPassword"
                      type="password"
                      placeholder="••••••••"
                      value={form.confirmPassword}
                      onChange={handleChange}
                  />
                </div>

                <Button type="submit" className="w-full">
                  회원가입
                </Button>
              </form>
            </CardContent>

            <CardFooter className="flex flex-col gap-4">
              <div className="text-sm text-center text-muted-foreground">
                이미 계정이 있으신가요?{" "}
                <Link href="/login" className="text-primary hover:underline">
                  로그인
                </Link>
              </div>
            </CardFooter>
          </Card>
        </main>
      </div>
  )
}