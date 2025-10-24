"use client"

import { useState, useEffect } from "react"
import { Header } from "@/components/header"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Loader2 } from "lucide-react"

// 필사 기록 데이터의 타입을 정의합니다.
interface TranscriptionRes {
  transcriptionId: number
  content: string
  createdAt: string
}

// 날짜를 "N일 전" 형식으로 변환하는 함수
const timeAgo = (dateString: string): string => {
  const now = new Date()
  const past = new Date(dateString)
  const diffInSeconds = Math.floor((now.getTime() - past.getTime()) / 1000)
  const diffInDays = Math.floor(diffInSeconds / (60 * 60 * 24))

  if (diffInDays === 0) return "오늘"
  if (diffInDays === 1) return "1일 전"
  return `${diffInDays}일 전`
}

export default function TranscriptionListPage() {
  const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL
  const [transcriptions, setTranscriptions] = useState<TranscriptionRes[]>([])
  const [page, setPage] = useState(0) // Spring Pageable은 0부터 시작
  const [isLastPage, setIsLastPage] = useState(false)
  const [isLoading, setIsLoading] = useState(true)

  // 데이터를 불러오는 함수
  const fetchTranscriptions = async (pageNum: number) => {
    setIsLoading(true)
    const token = localStorage.getItem("accessToken")
    if (!token) {
      setIsLoading(false)
      return
    }

    try {
      const response = await fetch(`${baseUrl}/transcriptions/me?page=${pageNum}&size=20`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      if (!response.ok) throw new Error("Network response was not ok")

      const result = await response.json()
      setTranscriptions((prev) => [...prev, ...result.content])
      setIsLastPage(result.last) // Spring Pageable 응답의 'last' 필드 사용
    } catch (error) {
      console.error("Failed to fetch transcriptions:", error)
    } finally {
      setIsLoading(false)
    }
  }

  // 컴포넌트가 처음 렌더링될 때 첫 페이지 데이터를 불러옵니다.
  useEffect(() => {
    fetchTranscriptions(0)
  }, [])

  // '더 보기' 버튼 클릭 핸들러
  const handleLoadMore = () => {
    const nextPage = page + 1
    setPage(nextPage)
    fetchTranscriptions(nextPage)
  }

  return (
    <div className="min-h-screen">
      <Header />

      <main className="container py-8 md:py-12 max-w-4xl">
        <div className="mb-8">
          <h1 className="text-3xl font-bold mb-2">내가 필사한 문장</h1>
          <p className="text-muted-foreground">마음의 흔적들을 다시 만나보세요.</p>
        </div>

        <div className="space-y-4">
          {transcriptions.map((item) => (
            <Card key={item.transcriptionId} className="p-6">
              <blockquote className="space-y-3">
                <p className="text-lg leading-relaxed">"{item.content}"</p>
                <footer className="text-right">
                  <span className="text-sm text-muted-foreground">{timeAgo(item.createdAt)}</span>
                </footer>
              </blockquote>
            </Card>
          ))}
        </div>

        {/* 로딩 중이면서 데이터가 없을 때만 스켈레톤 UI를 보여줄 수 있습니다. */}
        {isLoading && transcriptions.length === 0 && <p>불러오는 중...</p>}

        <div className="mt-8 flex justify-center">
          {!isLastPage && (
            <Button onClick={handleLoadMore} disabled={isLoading} size="lg">
              {isLoading && <Loader2 className="mr-2 h-5 w-5 animate-spin" />}
              {isLoading ? "불러오는 중..." : "더 보기"}
            </Button>
          )}
          {isLastPage && transcriptions.length > 0 && (
            <p className="text-muted-foreground">마지막 기록입니다.</p>
          )}
        </div>
      </main>
    </div>
  )
}