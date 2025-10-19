import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Header } from "@/components/header"
import { BarChart3, PenLine } from "lucide-react"

const emotions = [
  { emoji: "😊", label: "행복해요", color: "hover:bg-yellow-100" },
  { emoji: "😂", label: "즐거워요", color: "hover:bg-orange-100" },
  { emoji: "😌", label: "평온해요", color: "hover:bg-green-100" },
  { emoji: "😐", label: "그저그래요", color: "hover:bg-gray-100" },
  { emoji: "😔", label: "우울해요", color: "hover:bg-blue-100" },
  { emoji: "😢", label: "슬퍼요", color: "hover:bg-indigo-100" },
  { emoji: "😭", label: "힘들어요", color: "hover:bg-purple-100" },
  { emoji: "😤", label: "화나요", color: "hover:bg-red-100" },
]

export default function HomePage() {
  return (
    <div className="min-h-screen">
      <Header />

      <main className="container py-12 md:py-20">
        {/* Hero Section */}
        <div className="max-w-3xl mx-auto text-center mb-16">
          <h1 className="text-4xl md:text-5xl font-bold mb-6 text-balance">오늘의 감정을 기록하세요</h1>
          <p className="text-lg text-muted-foreground text-pretty leading-relaxed">
            POVI는 당신의 감정을 이해하고 위로받을 수 있는 공간입니다.
            <br />
            혼자가 아닌, 함께 나누는 감정 다이어리
          </p>
        </div>

        {/* Daily Quote */}
        <Card className="max-w-2xl mx-auto p-8 md:p-12 mb-12 bg-gradient-to-br from-accent/20 to-secondary/30 border-none">
          <blockquote className="text-center">
            <p className="text-xl md:text-2xl font-medium mb-4 text-balance leading-relaxed">
              "감정을 표현하는 것은 약함이 아니라 용기입니다"
            </p>
            <footer className="text-sm text-muted-foreground">오늘의 명언</footer>
          </blockquote>
        </Card>

        {/* Action Buttons */}
        <div className="flex flex-col sm:flex-row gap-4 justify-center items-center mb-20">
          <Button size="lg" className="w-full sm:w-auto gap-2" asChild>
            <Link href="/diaryEntry/new">
              <PenLine className="h-5 w-5" />
              다이어리 쓰기
            </Link>
          </Button>
          <Button size="lg" variant="outline" className="w-full sm:w-auto gap-2 bg-transparent" asChild>
            <Link href="/calendar">
              <BarChart3 className="h-5 w-5" />
              감정 리포트 보기
            </Link>
          </Button>
        </div>

        {/* Feature Cards */}
        <div className="grid md:grid-cols-5 gap-6 max-w-6xl mx-auto">
          <Card className="p-6 hover:shadow-lg transition-shadow">
            <div className="text-4xl mb-4">📝</div>
            <h3 className="text-lg font-semibold mb-2">감정 기록</h3>
            <p className="text-sm text-muted-foreground leading-relaxed">
              글과 이미지로 오늘의 감정을 자유롭게 표현하세요
            </p>
          </Card>

          <Card className="p-6 hover:shadow-lg transition-shadow">
            <div className="text-4xl mb-4">🤖</div>
            <h3 className="text-lg font-semibold mb-2">AI 분석</h3>
            <p className="text-sm text-muted-foreground leading-relaxed">
              당신의 감정 패턴을 분석하고 이해를 도와드려요
            </p>
          </Card>

          <Card className="p-6 hover:shadow-lg transition-shadow">
            <div className="text-4xl mb-4">💬</div>
            <h3 className="text-lg font-semibold mb-2">익명 공감</h3>
            <p className="text-sm text-muted-foreground leading-relaxed">
              비슷한 감정을 가진 사람들과 위로를 나눠보세요
            </p>
          </Card>

          <Card className="p-6 hover:shadow-lg transition-shadow">
            <div className="text-4xl mb-4">✨</div>
            <h3 className="text-lg font-semibold mb-2">명언</h3>
            <p className="text-sm text-muted-foreground leading-relaxed">
              매일 새로운 명언을 필사하며 마음을 가다듬어요
            </p>
          </Card>

          <Card className="p-6 hover:shadow-lg transition-shadow">
            <div className="text-4xl mb-4">🎯</div>
            <h3 className="text-lg font-semibold mb-2">오늘의 미션</h3>
            <p className="text-sm text-muted-foreground leading-relaxed">AI가 추천하는 맞춤 미션으로 하루를 풍요롭게</p>
          </Card>
        </div>
      </main>
    </div>
  )
}
