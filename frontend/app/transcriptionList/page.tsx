"use client"

import { useState, useEffect } from "react"
import { Header } from "@/components/header"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Loader2 } from "lucide-react"
import { TranscriptionDetail } from "@/types/Transcription"

const timeAgo = (dateString: string): string => {
    const now = new Date();
    const past = new Date(dateString);
  
    // 시간, 분, 초를 모두 0으로 만들어 날짜만 비교하도록 설정
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const pastDate = new Date(past.getFullYear(), past.getMonth(), past.getDate());
  
    const diffTime = today.getTime() - pastDate.getTime();
    const diffInDays = diffTime / (1000 * 60 * 60 * 24);
  
    if (diffInDays === 0) return "오늘";
    if (diffInDays === 1) return "1일 전";
    return `${diffInDays}일 전`;
  };

export default function TranscriptionListPage() {
  const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL
  const [transcriptions, setTranscriptions] = useState<TranscriptionDetail[]>([]);
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
      const response = await fetch(`${baseUrl}/transcriptions/me?size=20`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      if (!response.ok) throw new Error("Network response was not ok")

      const result = await response.json()
      const newList = result.transcriptionList || [];
      
      if (pageNum === 0) {
        // 첫 페이지 로드 시에는 데이터를 완전히 교체합니다.
        setTranscriptions(newList);
      } else {
        // '더 보기' 클릭 시에는 기존 데이터에 이어붙입니다.
        setTranscriptions((prev) => [...prev, ...newList]);
      }

     if (newList.length < 20) {
        setIsLastPage(true);
      }
    } catch (error) {
      console.error("Failed to fetch transcriptions:", error);
    } finally {
      setIsLoading(false);
    }
  };

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
      <main className="container max-w-4xl py-8 md:py-12">
        <div className="mb-8">
          <h1 className="mb-2 text-3xl font-bold">내가 필사한 문장</h1>
          <p className="text-muted-foreground">마음의 흔적들을 다시 만나보세요.</p>
        </div>

        {/* 5. 렌더링 로직을 수정하여 DTO의 모든 필드를 활용합니다. */}
        <div className="space-y-4">
          {transcriptions.map((item) => (
            <Card key={item.transcriptionId} className="p-6">
              <blockquote className="space-y-4">
                {/* 내가 필사한 내용 */}
                <p className="text-lg leading-relaxed">"{item.content}"</p>
                {/* 원문과 저자 정보 */}
                <footer className="text-right text-sm text-muted-foreground">
                  <p> - "{item.quoteContent}" / {item.quoteAuthor}</p>
                </footer>
                {/* 작성 시간 */}
                <div className="text-right">
                  <span className="text-xs text-muted-foreground">{timeAgo(item.createdAt)}</span>
                </div>
              </blockquote>
            </Card>
          ))}
        </div>

        {/* 로딩 및 페이지네이션 UI */}
        {isLoading && <div className="mt-8 flex justify-center"><Loader2 className="h-8 w-8 animate-spin" /></div>}
        
        <div className="mt-8 flex justify-center">
          {!isLastPage && !isLoading && transcriptions.length > 0 && (
            <Button onClick={handleLoadMore} size="lg">
              더 보기
            </Button>
          )}
          {isLastPage && transcriptions.length > 0 && (
            <p className="text-muted-foreground">마지막 기록입니다.</p>
          )}
        </div>
      </main>
    </div>
  );
}