import { Header } from "@/components/header"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { ArrowLeft } from "lucide-react"
import Link from "next/link"

const following = [
  { id: 1, name: "사용자1", bio: "감정을 기록하는 중" },
  { id: 2, name: "사용자2", bio: "일상을 담아요" },
  { id: 3, name: "사용자3", bio: "행복을 찾아가는 중" },
  { id: 4, name: "사용자4", bio: "감성 다이어리" },
  { id: 5, name: "사용자5", bio: "오늘도 기록"},
  { id: 6, name: "사용자6", bio: "안녕하세요"},
  { id: 7, name: "사용자7", bio: "반가워요"},
  { id: 8, name: "사용자8", bio: "잘 부탁드려요"},
]

export default function FollowingPage() {
  return (
    <div className="min-h-screen">
      <Header />

      <main className="container py-8 md:py-12 max-w-4xl">
        <div className="mb-8">
          <Button variant="ghost" className="mb-4" asChild>
            <Link href="/profile">
              <ArrowLeft className="h-4 w-4 mr-2" />
              프로필로 돌아가기
            </Link>
          </Button>
          <h1 className="text-3xl font-bold mb-2">팔로잉</h1>
          <p className="text-muted-foreground">내가 팔로우하는 사람들</p>
        </div>

        <div className="space-y-4">
          {following.map((user) => (
            <Card key={user.id} className="p-6">
              <div className="flex items-center gap-4">
                <Avatar className="h-12 w-12">
                  <AvatarImage src={`/generic-placeholder-graphic.png?height=48&width=48`} />
                  <AvatarFallback>U{user.id}</AvatarFallback>
                </Avatar>
                <div className="flex-1">
                  <p className="font-medium">{user.name}</p>
                  <p className="text-sm text-muted-foreground">{user.bio}</p>
                </div>
                <Button size="sm" variant="outline">
                  팔로잉
                </Button>
              </div>
            </Card>
          ))}
        </div>
      </main>
    </div>
  )
}
