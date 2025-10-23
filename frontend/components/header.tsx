"use client"

import Link from "next/link"
import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Heart, BookOpen, Users, Sparkles, User, Target } from "lucide-react"

export function Header() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const router = useRouter()

  useEffect(() => {
    const accessToken = localStorage.getItem("accessToken")
    setIsLoggedIn(!!accessToken)
  }, [])

  const handleLogout = () => {
    localStorage.removeItem("accessToken")
    localStorage.removeItem("refreshToken")
    setIsLoggedIn(false)
    router.push("/login")
  }

  return (
      <header className="sticky top-0 z-50 w-full border-b border-border/40 bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="container flex h-16 items-center justify-between">
          {/* 로고 */}
          <Link href="/" className="flex items-center gap-2">
            <Heart className="h-6 w-6 text-primary fill-primary" />
            <span className="text-2xl font-semibold tracking-tight">POVI</span>
          </Link>

          {/* 메뉴 */}
          <nav className="hidden md:flex items-center gap-6">
            <Link
                href="/diary-posts"
                className="flex items-center gap-2 text-sm font-medium text-muted-foreground hover:text-foreground transition-colors"
            >
              <BookOpen className="h-4 w-4" />
              다이어리
            </Link>
            <Link
                href="/community"
                className="flex items-center gap-2 text-sm font-medium text-muted-foreground hover:text-foreground transition-colors"
            >
              <Users className="h-4 w-4" />
              커뮤니티
            </Link>
            <Link
                href="/quotes"
                className="flex items-center gap-2 text-sm font-medium text-muted-foreground hover:text-foreground transition-colors"
            >
              <Sparkles className="h-4 w-4" />
              명언
            </Link>
            <Link
                href="/missions"
                className="flex items-center gap-2 text-sm font-medium text-muted-foreground hover:text-foreground transition-colors"
            >
              <Target className="h-4 w-4" />
              오늘의 미션
            </Link>
            <Link
                href="/profile"
                className="flex items-center gap-2 text-sm font-medium text-muted-foreground hover:text-foreground transition-colors"
            >
              <User className="h-4 w-4" />
              마이페이지
            </Link>
          </nav>

          {/* 로그인 상태에 따른 버튼 */}
          <div className="flex items-center gap-3">
            {isLoggedIn ? (
                <Button variant="destructive" size="sm" onClick={handleLogout}>
                  로그아웃
                </Button>
            ) : (
                <>
                  <Button variant="ghost" size="sm" asChild>
                    <Link href="/login">로그인</Link>
                  </Button>
                  <Button size="sm" asChild>
                    <Link href="/signup">회원가입</Link>
                  </Button>
                </>
            )}
          </div>
        </div>
      </header>
  )
}