"use client"

import { useState } from "react"
import { Header } from "@/components/header"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { DiaryDetailDialog } from "@/components/diaryEntry-detail-dialog"
import Link from "next/link"
import { PenLine, Calendar, ImageIcon } from "lucide-react"

const recentDiaries = [
  {
    id: 1,
    date: "2025년 1월 15일",
    emotion: "😊",
    title: "좋은 하루였어요",
    preview: "오늘은 친구들과 즐거운 시간을 보냈어요. 오랜만에 만나서 이야기를 나누니...",
    content:
      "오늘은 친구들과 즐거운 시간을 보냈어요. 오랜만에 만나서 이야기를 나누니 마음이 따뜻해졌어요. 함께 맛있는 음식도 먹고 추억도 쌓았습니다. 이런 순간들이 정말 소중하다는 걸 느꼈어요.",
    visibility: "private",
    hasImage: true,
    allowComments: false,
  },
  {
    id: 2,
    date: "2025년 1월 14일",
    emotion: "😌",
    title: "평온한 하루",
    preview: "집에서 책을 읽으며 조용히 시간을 보냈어요. 가끔은 이런 시간이 필요한 것 같아요...",
    content:
      "집에서 책을 읽으며 조용히 시간을 보냈어요. 가끔은 이런 시간이 필요한 것 같아요. 혼자만의 시간을 가지면서 마음을 정리할 수 있었습니다.",
    visibility: "friends",
    hasImage: false,
    allowComments: true,
  },
]

const friendDiaries = [
  {
    id: 1,
    author: "익명의 토끼",
    date: "2025년 1월 15일",
    emotion: "😊",
    title: "행복한 하루",
    preview: "오늘 정말 좋은 일이 있었어요. 작은 것에 감사하게 되네요...",
    content:
      "오늘 정말 좋은 일이 있었어요. 작은 것에 감사하게 되네요. 아침에 일어나서 창문을 열었을 때 따뜻한 햇살이 들어왔고, 그 순간이 너무 행복했어요.",
    likes: 12,
    comments: 3,
    hasImage: true,
    allowComments: true,
  },
  {
    id: 2,
    author: "익명의 새",
    date: "2025년 1월 14일",
    emotion: "😌",
    title: "평온한 오후",
    preview: "카페에서 여유로운 시간을 보냈어요. 이런 순간이 소중해요...",
    content:
      "카페에서 여유로운 시간을 보냈어요. 이런 순간이 소중해요. 좋아하는 음악을 들으며 커피를 마시는 시간이 정말 평화로웠습니다.",
    likes: 8,
    comments: 1,
    hasImage: false,
    allowComments: true,
  },
]

const allPublicDiaries = [
  {
    id: 1,
    author: "익명의 토끼",
    date: "2025년 1월 15일",
    emotion: "😊",
    title: "행복한 하루",
    preview: "오늘 정말 좋은 일이 있었어요. 작은 것에 감사하게 되네요...",
    content:
      "오늘 정말 좋은 일이 있었어요. 작은 것에 감사하게 되네요. 아침에 일어나서 창문을 열었을 때 따뜻한 햇살이 들어왔고, 그 순간이 너무 행복했어요.",
    likes: 24,
    comments: 8,
    hasImage: true,
    allowComments: true,
  },
  {
    id: 2,
    author: "익명의 새",
    date: "2025년 1월 14일",
    emotion: "😌",
    title: "평온한 오후",
    preview: "카페에서 여유로운 시간을 보냈어요. 이런 순간이 소중해요...",
    content:
      "카페에서 여유로운 시간을 보냈어요. 이런 순간이 소중해요. 좋아하는 음악을 들으며 커피를 마시는 시간이 정말 평화로웠습니다.",
    likes: 15,
    comments: 4,
    hasImage: false,
    allowComments: true,
  },
  {
    id: 3,
    author: "익명의 고양이",
    date: "2025년 1월 13일",
    emotion: "🥰",
    title: "따뜻한 순간",
    preview: "가족들과 함께한 저녁 시간이 너무 좋았어요...",
    content:
      "가족들과 함께한 저녁 시간이 너무 좋았어요. 함께 식사를 하면서 이야기를 나누는 시간이 정말 소중하다는 걸 느꼈습니다. 이런 평범한 일상이 가장 큰 행복이에요.",
    likes: 32,
    comments: 12,
    hasImage: true,
    allowComments: true,
  },
]

export default function DiaryListPage() {
  const [selectedYear, setSelectedYear] = useState("2025")
  const [selectedMonth, setSelectedMonth] = useState("all")
  const [selectedDiary, setSelectedDiary] = useState<any>(null)
  const [dialogOpen, setDialogOpen] = useState(false)

  const weeklyAvgEmotion = "긍정적"

  const openDiaryDetail = (diaryEntry: any) => {
    setSelectedDiary(diaryEntry)
    setDialogOpen(true)
  }

  return (
    <div className="min-h-screen">
      <Header />

      <main className="container py-8 md:py-12 max-w-5xl">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
          <div>
            <h1 className="text-3xl font-bold mb-2">다이어리</h1>
            <p className="text-muted-foreground">감정의 기록들을 돌아보세요</p>
          </div>
          <div className="flex gap-3">
            <Button variant="outline" asChild>
              <Link href="/calendar">
                <Calendar className="h-4 w-4 mr-2" />
                캘린더 보기
              </Link>
            </Button>
            <Button asChild>
              <Link href="/diaryEntry/new">
                <PenLine className="h-4 w-4 mr-2" />새 다이어리
              </Link>
            </Button>
          </div>
        </div>

        <Tabs defaultValue="my" className="space-y-6">
          <TabsList className="grid w-full max-w-md grid-cols-3">
            <TabsTrigger value="my">나의 다이어리</TabsTrigger>
            <TabsTrigger value="friends">친구 다이어리</TabsTrigger>
            <TabsTrigger value="all">다이어리 둘러보기</TabsTrigger>
          </TabsList>

          {/* My Diary Tab */}
          <TabsContent value="my" className="space-y-6">
            {/* Stats */}
            <div className="grid md:grid-cols-3 gap-4">
              <Card className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-muted-foreground mb-1">총 작성 일수</p>
                    <p className="text-2xl font-bold">42일</p>
                  </div>
                  <div className="text-4xl">📝</div>
                </div>
              </Card>
              <Card className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-muted-foreground mb-1">이번 주 기록</p>
                    <p className="text-2xl font-bold">5일</p>
                  </div>
                  <div className="text-4xl">🔥</div>
                </div>
              </Card>
              <Card className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-muted-foreground mb-1">일주일 평균 감정</p>
                    <p className="text-2xl font-bold">{weeklyAvgEmotion}</p>
                  </div>
                  <div className="text-4xl">😊</div>
                </div>
              </Card>
            </div>

            <div className="flex gap-3">
              <Select value={selectedYear} onValueChange={setSelectedYear}>
                <SelectTrigger className="w-32">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="2025">2025년</SelectItem>
                  <SelectItem value="2024">2024년</SelectItem>
                </SelectContent>
              </Select>
              <Select value={selectedMonth} onValueChange={setSelectedMonth}>
                <SelectTrigger className="w-32">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">전체</SelectItem>
                  <SelectItem value="1">1월</SelectItem>
                  <SelectItem value="2">2월</SelectItem>
                  <SelectItem value="3">3월</SelectItem>
                  <SelectItem value="4">4월</SelectItem>
                  <SelectItem value="5">5월</SelectItem>
                  <SelectItem value="6">6월</SelectItem>
                  <SelectItem value="7">7월</SelectItem>
                  <SelectItem value="8">8월</SelectItem>
                  <SelectItem value="9">9월</SelectItem>
                  <SelectItem value="10">10월</SelectItem>
                  <SelectItem value="11">11월</SelectItem>
                  <SelectItem value="12">12월</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* Recent Diaries */}
            <div className="space-y-4">
              {recentDiaries.map((diaryEntry) => (
                <Card
                  key={diaryEntry.id}
                  className="p-6 hover:shadow-lg transition-shadow cursor-pointer"
                  onClick={() => openDiaryDetail(diaryEntry)}
                >
                  <div className="flex gap-4">
                    <div className="text-4xl">{diaryEntry.emotion}</div>
                    <div className="flex-1">
                      <div className="flex items-start justify-between mb-2">
                        <div>
                          <h3 className="font-semibold text-lg mb-1">{diaryEntry.title}</h3>
                          <p className="text-sm text-muted-foreground">{diaryEntry.date}</p>
                        </div>
                        <span className="text-xs px-2 py-1 bg-muted rounded-full">
                          {diaryEntry.visibility === "private"
                            ? "비공개"
                            : diaryEntry.visibility === "friends"
                              ? "친구공개"
                              : "전체공개"}
                        </span>
                      </div>
                      <p className="text-muted-foreground leading-relaxed">{diaryEntry.preview}</p>
                    </div>
                    {diaryEntry.hasImage && (
                      <div className="flex-shrink-0 w-20 h-20 bg-muted rounded-lg flex items-center justify-center">
                        <ImageIcon className="h-8 w-8 text-muted-foreground" />
                      </div>
                    )}
                  </div>
                </Card>
              ))}
            </div>
          </TabsContent>

          <TabsContent value="friends" className="space-y-4">
            <div className="mb-4">
              <h2 className="text-xl font-semibold mb-2">친구들의 다이어리</h2>
              <p className="text-sm text-muted-foreground">팔로잉한 친구들이 공유한 감정을 확인하세요</p>
            </div>

            {friendDiaries.map((diaryEntry) => (
              <Card
                key={diaryEntry.id}
                className="p-6 hover:shadow-lg transition-shadow cursor-pointer"
                onClick={() => openDiaryDetail(diaryEntry)}
              >
                <div className="flex gap-4">
                  <div className="text-4xl">{diaryEntry.emotion}</div>
                  <div className="flex-1">
                    <div className="flex items-start justify-between mb-2">
                      <div>
                        <h3 className="font-semibold text-lg mb-1">{diaryEntry.title}</h3>
                        <p className="text-sm text-muted-foreground">
                          {diaryEntry.author} • {diaryEntry.date}
                        </p>
                      </div>
                    </div>
                    <p className="text-muted-foreground leading-relaxed mb-4">{diaryEntry.preview}</p>
                    <div className="flex items-center gap-4">
                      <span className="text-sm text-muted-foreground">❤️ {diaryEntry.likes}</span>
                      <span className="text-sm text-muted-foreground">💬 {diaryEntry.comments}</span>
                    </div>
                  </div>
                  {diaryEntry.hasImage && (
                    <div className="flex-shrink-0 w-20 h-20 bg-muted rounded-lg flex items-center justify-center">
                      <ImageIcon className="h-8 w-8 text-muted-foreground" />
                    </div>
                  )}
                </div>
              </Card>
            ))}
          </TabsContent>

          {/* All Public Diaries Tab */}
          <TabsContent value="all" className="space-y-4">
            <div className="mb-4">
              <h2 className="text-xl font-semibold mb-2">모두의 다이어리</h2>
              <p className="text-sm text-muted-foreground">최근 일주일 사이 전체 공개된 다이어리를 둘러보세요</p>
            </div>

            {allPublicDiaries.map((diaryEntry) => (
              <Card
                key={diaryEntry.id}
                className="p-6 hover:shadow-lg transition-shadow cursor-pointer"
                onClick={() => openDiaryDetail(diaryEntry)}
              >
                <div className="flex gap-4">
                  <div className="text-4xl">{diaryEntry.emotion}</div>
                  <div className="flex-1">
                    <div className="flex items-start justify-between mb-2">
                      <div>
                        <h3 className="font-semibold text-lg mb-1">{diaryEntry.title}</h3>
                        <p className="text-sm text-muted-foreground">
                          {diaryEntry.author} • {diaryEntry.date}
                        </p>
                      </div>
                    </div>
                    <p className="text-muted-foreground leading-relaxed mb-4">{diaryEntry.preview}</p>
                    <div className="flex items-center gap-4">
                      <span className="text-sm text-muted-foreground">❤️ {diaryEntry.likes}</span>
                      <span className="text-sm text-muted-foreground">💬 {diaryEntry.comments}</span>
                    </div>
                  </div>
                  {diaryEntry.hasImage && (
                    <div className="flex-shrink-0 w-20 h-20 bg-muted rounded-lg flex items-center justify-center">
                      <ImageIcon className="h-8 w-8 text-muted-foreground" />
                    </div>
                  )}
                </div>
              </Card>
            ))}
          </TabsContent>
        </Tabs>

        {/* Diary Detail Dialog */}
        {selectedDiary && <DiaryDetailDialog open={dialogOpen} onOpenChange={setDialogOpen} diaryEntry={selectedDiary} />}
      </main>
    </div>
  )
}
