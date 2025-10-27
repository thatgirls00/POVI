"use client"

import { useState, useEffect } from "react"
import { Header } from "@/components/header"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Heart, MessageCircle, Flag, Bookmark } from "lucide-react"
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs"
import apiClient from "@/lib/axios"
import { CommunityDetailDialog } from "@/components/community-detail-dialog"

// ✅ PostListResponse 기반 타입
interface Post {
  postId: number
  title: string
  content: string
  authorNickname: string
  createdAt: string
  emoticon: string
  likeCount: number
  commentCount: number
}

interface PageResponse<T> {
  content: T[]
  totalPages: number
  totalElements: number
  size: number
  number: number
}

export default function CommunityPage() {
  const [posts, setPosts] = useState<Post[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [filter, setFilter] = useState<"latest" | "popular">("latest")
  const [selectedPost, setSelectedPost] = useState<Post | null>(null)
  const [dialogOpen, setDialogOpen] = useState(false)

  const fetchPosts = async () => {
    setIsLoading(true)
    try {
      const sortParam = filter === "popular" ? "likeCount,desc" : "createdAt,desc"
      const res = await apiClient.get<PageResponse<Post>>("/posts", {
        params: { page: 0, size: 10, sort: sortParam },
      })
      setPosts(res.data.content)
    } catch (error) {
      console.error("게시글 목록 불러오기 실패:", error)
    } finally {
      setIsLoading(false)
    }
  }

  // 감정 enum → 실제 이모지 매핑
  const emotionMap: Record<string, string> = {
    HAPPY: "😊",
    SAD: "😔",
    ANGRY: "😡",
    ANXIOUS: "😰",
    THANKFUL: "🥰",
    TIRED: "😭",
    CALM: "😌",
    NORMAL: "😐",
  }


  useEffect(() => {
    fetchPosts()
  }, [filter])

  const openPostDetail = (post: Post) => {
    setSelectedPost(post)
    setDialogOpen(true)
  }

  return (
      <div className="min-h-screen">
        <Header />
        <main className="container py-8 md:py-12 max-w-5xl">
          <h1 className="text-2xl font-bold mb-6">커뮤니티</h1>

          <Tabs
              value={filter}
              onValueChange={(v: "latest" | "popular") => setFilter(v)}
              className="mb-6"
          >
            <TabsList className="grid w-full max-w-md grid-cols-2">
              <TabsTrigger value="latest">최신글</TabsTrigger>
              <TabsTrigger value="popular">인기글</TabsTrigger>
            </TabsList>
          </Tabs>


          {isLoading ? (
              <p>게시글을 불러오는 중입니다...</p>
          ) : posts.length === 0 ? (
              <p>아직 작성된 글이 없습니다.</p>
          ) : (
              <div className="space-y-4">
                {posts.map((post) => (
                    <Card
                        key={post.postId}
                        className="p-6 hover:shadow-lg transition-shadow cursor-pointer"
                        onClick={() => openPostDetail(post)}
                    >
                      <div className="flex gap-4">
                        <div className="text-4xl">{post.emoticon}
                          {emotionMap[post.emoticon] || "🙂"}
                        </div>
                        <div className="flex-1">
                          <div className="flex items-start justify-between mb-2">
                            <div>
                              <h3 className="font-semibold text-lg mb-1">{post.title}</h3>
                              <div className="flex items-center gap-2 text-sm text-muted-foreground">
                                <span>{post.authorNickname}</span>
                                <span>•</span>
                                <span>{new Date(post.createdAt).toLocaleDateString("ko-KR")}</span>
                              </div>
                            </div>
                            <Button
                                size="sm"
                                variant="ghost"
                                className="text-muted-foreground hover:text-destructive"
                                onClick={(e: React.MouseEvent<HTMLButtonElement>) => {
                                  e.stopPropagation()
                                  console.log("신고 기능 준비 중")
                                }}
                            >
                              <Flag className="h-4 w-4" />
                            </Button>
                          </div>

                          <p className="text-muted-foreground leading-relaxed mb-4">{post.content}</p>

                          <div className="flex items-center gap-4">
                            <button
                                className="flex items-center gap-1 text-sm text-muted-foreground hover:text-primary"
                                onClick={(e: React.MouseEvent<HTMLButtonElement>) => e.stopPropagation()}
                            >
                              <Heart className="h-4 w-4" />
                              <span>{post.likeCount}</span>
                            </button>
                            <button
                                className="flex items-center gap-1 text-sm text-muted-foreground hover:text-primary"
                                onClick={(e: React.MouseEvent<HTMLButtonElement>) => e.stopPropagation()}
                            >
                              <MessageCircle className="h-4 w-4" />
                              <span>{post.commentCount}</span>
                            </button>
                            <button
                                className="flex items-center gap-1 text-sm text-muted-foreground hover:text-primary"
                                onClick={(e: React.MouseEvent<HTMLButtonElement>) => e.stopPropagation()}
                            >
                              <Bookmark className="h-4 w-4" />
                            </button>
                          </div>
                        </div>
                      </div>
                    </Card>
                ))}
              </div>
          )}

          {selectedPost && (
              <CommunityDetailDialog
                  open={dialogOpen}
                  onOpenChange={setDialogOpen}
                  post={{
                    id: selectedPost.postId,
                    authorNickname: selectedPost.authorNickname,
                    createdAt: new Date(selectedPost.createdAt).toISOString(),
                    emoticon: selectedPost.emoticon,
                    title: selectedPost.title,
                    content: selectedPost.content,
                    likeCount: selectedPost.likeCount,
                    commentCount: selectedPost.commentCount,
                  }}
              />
          )}
        </main>
      </div>
  )
}
