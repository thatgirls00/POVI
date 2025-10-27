"use client"

import { useState , useEffect} from "react"
import { Header } from "@/components/header"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Textarea } from "@/components/ui/textarea"
import { PenLine, Check, Sparkles, Loader2 } from "lucide-react"
import { QuoteRes } from "@/types/Quote";
import { TranscriptionRes } from "@/types/Transcription";
import Link from "next/link"

const quotes = [
  {
    text: "감정을 표현하는 것은 약함이 아니라 용기입니다",
    author: "브레네 브라운",
  },
  {
    text: "어둠 속에서도 빛을 찾을 수 있다면, 그것이 진정한 행복입니다",
    author: "알버스 덤블도어",
  },
  {
    text: "당신이 겪고 있는 폭풍은 당신을 파괴하는 것이 아니라 더 강하게 만듭니다",
    author: "익명",
  },
  {
    text: "오늘의 작은 진전이 내일의 큰 변화를 만듭니다",
    author: "익명",
  },
  {
    text: "완벽하지 않아도 괜찮아요. 있는 그대로의 당신이 충분합니다",
    author: "익명",
  },
]

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

export default function QuotesPage() {
  const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL
  const [currentQuote] = useState(quotes[0])
  const [isCalligraphy, setIsCalligraphy] = useState(false)
  const [calligraphyText, setCalligraphyText] = useState("")
  const [isSaved, setIsSaved] = useState(false)
  const [quote, setQuote] = useState<QuoteRes | null>(null);
  const [myTranscriptions, setMyTranscriptions] = useState<TranscriptionRes[]>([])
  
  useEffect(() => {
    const fetchTodaysQuote = async () => {
      try {
        // 백엔드 API에 GET 요청을 보냅니다.
        const response = await fetch(`${baseUrl}/quotes/today`);
        const data = await response.json();
        // 응답받은 데이터로 상태를 업데이트합니다.
        console.log(data)
        setQuote(data);
      } catch (error) {
        console.error("오늘의 명언을 가져오는 데 실패했습니다:", error);
      }
    };
    fetchTodaysQuote();
  }, []); // 의존성 배열이 비어있으면 최초 1회만 실행됩니다.

  useEffect(() => {
        const fetchMyTranscriptions = async () => {
          // 실제 인증 로직에 따라 토큰을 가져옵니다. (예: localStorage)
          const token = localStorage.getItem("accessToken")
          if (!token) return // 토큰이 없으면 요청하지 않음
    
          try {
            const response = await fetch(`${baseUrl}/transcriptions/me?size=4`, {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            })
    //         if (!response.ok) throw new Error("Network response was not ok")
            const data = await response.json();
        // 백엔드 DTO 구조에 맞춰 "transcriptionList"에서 데이터를 가져옵니다.
        setMyTranscriptions(data.transcriptionList || []);
      } catch (error) {
        console.error(error);
        setMyTranscriptions([]);
      }
    };
    fetchMyTranscriptions();
  }, [baseUrl]);

  const saveCalligraphy = async () => {
        if (!quote) return;
    
        setIsSaved(true)
        const token = localStorage.getItem("accessToken")
        if (!token) {
          alert("로그인이 필요합니다.");
          setIsSaved(false);
          return;
        }
    
        try {
          const response = await fetch(`${baseUrl}/transcriptions/${quote.quoteId}`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({ content: calligraphyText }),
          })
    
          if (!response.ok) {
            throw new Error("필사 저장에 실패했습니다.")
          }
    
          const newTranscription = await response.json()
    
          // 저장 성공 시, 상태를 업데이트하여 화면을 즉시 갱신합니다.
          setIsCalligraphy(false)
          // 3-2. 기존 목록의 맨 앞에 새 기록을 추가하고, 4개로 개수를 유지합니다.
          setMyTranscriptions((prev) => [newTranscription, ...prev].slice(0, 4))
        } catch (error) {
          console.error(error)
          alert("오류가 발생했습니다. 다시 시도해주세요.")
        } finally {
            setIsSaved(false)
        }
      }

  const startCalligraphy = () => {
    setIsCalligraphy(true)
    setCalligraphyText("")
  }

  if (!quote) {
    return (
      <div className="flex h-screen items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  const transcriptionGridClasses = (() => {
    const count = myTranscriptions.length;
    if (count === 1) {
      // 아이템이 1개일 때 왼쪽 정렬
      return "grid md:grid-cols-2 gap-4 md:justify-start";
    } else if (count === 2) {
      // 아이템이 2개일 때 양 옆으로 정렬 (flex justify-between 대신 grid-cols-2로 간격 유지)
      return "grid md:grid-cols-2 gap-4 md:justify-between"; // grid-cols-2는 이미 양 옆으로 배치
    } else if (count >= 3) {
      // 아이템이 3개 이상일 때 (최대 4개)는 기본 grid-cols-2 유지
      return "grid md:grid-cols-2 gap-4";
    }
    return "grid md:grid-cols-2 gap-4"; // 기본값 (없거나 기타 경우)
  })();

  return (
    <div className="min-h-screen">
      <Header />

      <main className="container py-8 md:py-12 max-w-4xl">
        <div className="mb-8">
          <h1 className="text-3xl font-bold mb-2">오늘의 명언</h1>
          <p className="text-muted-foreground">마음을 위로하는 따뜻한 문장을 만나보세요</p>
        </div>

        {!isCalligraphy ? (
          <div className="space-y-6">
            {/* Daily Quote */}
            <Card className="p-12 bg-gradient-to-br from-accent/30 to-secondary/40 border-none">
              <div className="flex justify-center mb-6">
                <Sparkles className="h-8 w-8 text-primary" />
              </div>
              <blockquote className="text-center space-y-6">
                <p className="text-2xl md:text-3xl font-medium leading-relaxed text-balance">{quote.message}</p>
                <footer className="text-base text-muted-foreground">- {quote.author}</footer>
              </blockquote>
            </Card>

            {/* Actions */}
            <div className="flex justify-center">
              <Button size="lg" className="gap-2" onClick={startCalligraphy}>
                <PenLine className="h-5 w-5" /> 필사하기
              </Button>
            </div>

            {/* Info Card */}
            <Card className="p-6 bg-muted/50">
              <h3 className="font-semibold mb-2">필사의 효과</h3>
              <p className="text-sm text-muted-foreground leading-relaxed">
                손으로 직접 문장을 베껴 쓰면 마음이 차분해지고 그 의미가 더 깊이 새겨집니다. 하루 한 문장, 마음을
                가다듬는 시간을 가져보세요.
              </p>
            </Card>

            {/* Recent Calligraphy */}
            <div>
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-xl font-semibold">내가 필사한 문장</h2>
                <Button variant="ghost" size="sm" asChild>
                  <Link href="/transcriptionList">전체 보기</Link>
                </Button>
              </div>
              {myTranscriptions.length > 0 ? (
                <div
 className={
   myTranscriptions.length > 1
     ? "grid gap-4 md:grid-cols-2" // 아이템이 2개 이상이면 Grid
     : "flex justify-start" // 아이템이 1개면 Flex + 왼쪽 정렬
 }
>
 {myTranscriptions.map(item => (
   <Card
     key={item.transcriptionId}
     // 아이템이 1개일 때 너비를 제한하여 어색하게 늘어나지 않도록 함
     className={`p-4 ${myTranscriptions.length === 1 ? "w-full md:w-[calc(50%-0.5rem)]" : ""}`}
   >
     <p className="mb-2 truncate text-sm leading-relaxed">{item.content}</p>
     <p className="text-xs text-muted-foreground">{timeAgo(item.createdAt)}</p>
   </Card>
 ))}
</div>
              ) : (
                <Card className="p-6 text-center text-muted-foreground">아직 필사한 문장이 없어요.</Card>
              )}
            </div>
          </div>
        ) : (
          <div className="space-y-6">
            {/* Original Quote */}
            <Card className="p-8 bg-secondary/30">
              <p className="text-center text-xl font-medium leading-relaxed text-balance">{quote.message}</p>
            </Card>

            {/* Calligraphy Input */}
            <Card className="p-6">
              <div className="mb-4">
                <h2 className="text-xl font-semibold mb-2">필사하기</h2>
                <p className="text-sm text-muted-foreground">위 문장을 천천히 베껴 써보세요</p>
              </div>
              <Textarea
                placeholder="여기에 문장을 베껴 쓰세요..."
                className="min-h-[200px] resize-none text-lg leading-relaxed"
                value={calligraphyText}
                onChange={(e) => setCalligraphyText(e.target.value)}
              />
              <div className="mt-4 flex items-center justify-between">
                <p className="text-sm text-muted-foreground">
                  {calligraphyText.length} / {quote.message.length} 글자
                </p>
                {calligraphyText === quote.message && (
                  <div className="flex items-center gap-2 text-sm text-primary">
                    <Check className="h-4 w-4" />
                    <span>완벽해요!</span>
                  </div>
                )}
              </div>
            </Card>

            {/* Actions */}
            <div className="flex gap-4">
            <Button
                size="lg"
                className="flex-1"
                onClick={saveCalligraphy}
                // 1. 제약 조건: 입력 내용이 명언과 다르면 비활성화
                disabled={calligraphyText !== quote.message || isSaved}
              >
                {isSaved && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                {isSaved ? "저장 중..." : "저장하기"}
              </Button>
              <Button size="lg" variant="outline" onClick={() => setIsCalligraphy(false)}>
                취소
              </Button>
            </div>

            {isSaved && (
              <Card className="p-6 bg-primary/10 border-primary/20">
                <div className="flex items-center gap-3">
                  <div className="flex-shrink-0 w-10 h-10 rounded-full bg-primary/20 flex items-center justify-center">
                    <Check className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <p className="font-medium mb-1">필사를 완료했어요!</p>
                    <p className="text-sm text-muted-foreground">마이페이지에서 다시 볼 수 있어요</p>
                  </div>
                </div>
              </Card>
            )}
          </div>
        )}
      </main>
    </div>
  )
}
