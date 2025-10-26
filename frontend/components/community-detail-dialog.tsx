"use client"

import { useEffect, useState } from "react"
import { Dialog, DialogContent } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Textarea } from "@/components/ui/textarea"
import { Heart, Bookmark, Flag, Trash2 } from "lucide-react"
import apiClient from "@/lib/axios"

interface CommentResponse {
  commentId: number
  authorName: string
  content: string
  likeCount: number
  createdAt: string
}

interface PostDetailResponse {
  postId: number
  title: string
  content: string
  emoticon: string
  authorNickname: string
  createdAt: string
  comments: CommentResponse[]
  photoUrls: string[]
  likeCount?: number
}

interface CommunityDetailDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  post: {
    id: number
    authorNickname: string
    createdAt: string
    emoticon: string
    title: string
    content: string
    likeCount: number
    commentCount: number
  }
}


// ✅ 프론트 전용 JWT 해석 함수
const getUserNicknameFromToken = (): string | null => {
  if (typeof window === "undefined") return null
  const token = localStorage.getItem("accessToken")
  if (!token) return null
  try {
    const payload = JSON.parse(atob(token.split(".")[1]))
    // 예시: JWT payload에 nickname 또는 sub 필드
    return payload.nickname || payload.name || payload.sub || null
  } catch {
    return null
  }
}

export function CommunityDetailDialog({ open, onOpenChange, post }: CommunityDetailDialogProps) {
  const [postDetail, setPostDetail] = useState<PostDetailResponse | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [isLiked, setIsLiked] = useState(false)
  const [isBookmarked, setIsBookmarked] = useState(false)
  const [comment, setComment] = useState("")
  const [likedComments, setLikedComments] = useState<number[]>([])
  const [currentUserNickname, setCurrentUserNickname] = useState<string | null>(null)

  // ✅ 현재 로그인 유저 닉네임 추출
  useEffect(() => {
    const nickname = getUserNicknameFromToken()
    setCurrentUserNickname(nickname)
  }, [])

  // ✅ 게시글 상세 불러오기
  const fetchPostDetail = async () => {
    try {
      const res = await apiClient.get<PostDetailResponse>(`/posts/${post.id}`)
      setPostDetail(res.data)
    } catch (error) {
      console.error("게시글 상세 불러오기 실패:", error)
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    if (open && post.id) {
      setIsLoading(true)
      fetchPostDetail()
    }
  }, [open, post.id])

  // ✅ 게시글 좋아요 즉시 반영
  const togglePostLike = async () => {
    if (!postDetail) return
    const prevLiked = isLiked
    const prevCount = postDetail.likeCount ?? 0
    setIsLiked(!prevLiked)
    setPostDetail({ ...postDetail, likeCount: prevLiked ? prevCount - 1 : prevCount + 1 })

    try {
      if (!prevLiked) await apiClient.post(`/posts/${postDetail.postId}/like`)
      else await apiClient.delete(`/posts/${postDetail.postId}/like`)
    } catch (error) {
      console.error("게시글 좋아요 처리 실패:", error)
      setIsLiked(prevLiked)
      setPostDetail({ ...postDetail, likeCount: prevCount })
    }
  }

  // ✅ 댓글 좋아요 즉시 반영
  const toggleCommentLike = async (commentId: number) => {
    if (!postDetail) return
    const alreadyLiked = likedComments.includes(commentId)

    setLikedComments((prev) =>
        alreadyLiked ? prev.filter((id) => id !== commentId) : [...prev, commentId]
    )
    setPostDetail({
      ...postDetail,
      comments: postDetail.comments.map((c) =>
          c.commentId === commentId
              ? { ...c, likeCount: c.likeCount + (alreadyLiked ? -1 : 1) }
              : c
      ),
    })

    try {
      if (alreadyLiked) await apiClient.delete(`/comments/${commentId}/like`)
      else await apiClient.post(`/comments/${commentId}/like`)
    } catch (error) {
      console.error("댓글 좋아요 처리 실패:", error)
    }
  }

  // ✅ 댓글 작성 (댓글 수 실시간 반영)
  const handleAddComment = async () => {
    if (!comment.trim() || !postDetail) return
    try {
      await apiClient.post(`/posts/${post.id}/comments`, { content: comment })
      setPostDetail({
        ...postDetail,
        comments: [
          ...postDetail.comments,
          {
            commentId: Date.now(),
            authorName: currentUserNickname ?? "나",
            content: comment,
            likeCount: 0,
            createdAt: new Date().toISOString(),
          },
        ],
      })
      setComment("")
    } catch (error) {
      console.error("댓글 작성 실패:", error)
    }
  }

  // ✅ 댓글 삭제 (내 댓글만 가능)
  const handleDeleteComment = async (commentId: number) => {
    if (!postDetail) return
    const prevComments = postDetail.comments
    setPostDetail({
      ...postDetail,
      comments: prevComments.filter((c) => c.commentId !== commentId),
    })
    try {
      await apiClient.delete(`/comments/${commentId}`)
    } catch (error) {
      console.error("댓글 삭제 실패:", error)
      // 실패 시 롤백
      setPostDetail({ ...postDetail, comments: prevComments })
    }
  }

  if (!open) return null

  return (
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
          {isLoading ? (
              <p>게시글을 불러오는 중입니다...</p>
          ) : !postDetail ? (
              <p>게시글 정보를 불러오지 못했습니다.</p>
          ) : (
              <div className="space-y-6">
                {/* Header */}
                <div className="flex items-start gap-4">
                  <div className="text-5xl">{postDetail.emoticon}</div>
                  <div className="flex-1">
                    <div className="flex items-start justify-between mb-2">
                      <div>
                        <h2 className="text-2xl font-bold mb-2">{postDetail.title}</h2>
                        <div className="flex items-center gap-2 text-sm text-muted-foreground">
                          <span>{postDetail.authorNickname}</span>
                          <span>•</span>
                          <span>{new Date(postDetail.createdAt).toLocaleDateString("ko-KR")}</span>
                        </div>
                      </div>
                      <Button size="sm" variant="ghost" className="text-muted-foreground hover:text-destructive">
                        <Flag className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </div>

                {/* Content */}
                <p className="text-base leading-relaxed whitespace-pre-wrap">{postDetail.content}</p>

                {/* Images */}
                {postDetail.photoUrls.length > 0 && (
                    <div className="grid grid-cols-2 gap-2">
                      {postDetail.photoUrls.map((url, idx) => (
                          <img
                              key={idx}
                              src={url}
                              alt={`photo-${idx}`}
                              className="rounded-md object-cover w-full aspect-square"
                          />
                      ))}
                    </div>
                )}

                {/* 게시글 좋아요 / 북마크 */}
                <div className="flex items-center gap-2 pt-4 border-t">
                  <Button variant="ghost" size="sm" onClick={togglePostLike}>
                    <Heart className={`h-4 w-4 mr-1 ${isLiked ? "fill-red-500 text-red-500" : ""}`} />
                    {postDetail.likeCount ?? 0}
                  </Button>
                  <Button variant="ghost" size="sm" onClick={() => setIsBookmarked(!isBookmarked)}>
                    <Bookmark className={`h-4 w-4 mr-1 ${isBookmarked ? "fill-current" : ""}`} />
                    북마크
                  </Button>
                </div>

                {/* 댓글 섹션 */}
                <div className="space-y-4 pt-4 border-t">
                  <h3 className="font-semibold">댓글 {postDetail.comments.length}</h3>

                  {/* 댓글 입력 */}
                  <div className="flex gap-3">
                    <Avatar className="h-8 w-8">
                      <AvatarImage src="/placeholder.svg" />
                      <AvatarFallback>나</AvatarFallback>
                    </Avatar>
                    <div className="flex-1 space-y-2">
                      <Textarea
                          placeholder="댓글을 입력하세요..."
                          value={comment}
                          onChange={(e) => setComment(e.target.value)}
                          rows={2}
                      />
                      <Button size="sm" disabled={!comment.trim()} onClick={handleAddComment}>
                        댓글 작성
                      </Button>
                    </div>
                  </div>

                  {/* 댓글 목록 */}
                  {postDetail.comments.map((c) => {
                    const isMyComment = currentUserNickname === c.authorName
                    return (
                        <Card key={c.commentId} className="p-4">
                          <div className="flex gap-3">
                            <Avatar className="h-8 w-8">
                              <AvatarImage src="/placeholder.svg" />
                              <AvatarFallback>{c.authorName[0]}</AvatarFallback>
                            </Avatar>
                            <div className="flex-1">
                              <div className="flex items-center justify-between mb-1">
                                <div className="flex items-center gap-2">
                                  <span className="font-medium text-sm">{c.authorName}</span>
                                  <span className="text-xs text-muted-foreground">
                              {new Date(c.createdAt).toLocaleDateString("ko-KR")}
                            </span>
                                </div>
                                {isMyComment && (
                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        className="h-6 w-6 p-0 text-destructive hover:text-destructive"
                                        onClick={() => handleDeleteComment(c.commentId)}
                                    >
                                      <Trash2 className="h-3 w-3" />
                                    </Button>
                                )}
                              </div>

                              <p className="text-sm leading-relaxed mb-2">{c.content}</p>

                              <button
                                  className={`flex items-center gap-1 text-xs transition-colors ${
                                      likedComments.includes(c.commentId)
                                          ? "text-red-500"
                                          : "text-muted-foreground hover:text-primary"
                                  }`}
                                  onClick={() => toggleCommentLike(c.commentId)}
                              >
                                <Heart className="h-3 w-3" />
                                {c.likeCount}
                              </button>
                            </div>
                          </div>
                        </Card>
                    )
                  })}
                </div>
              </div>
          )}
        </DialogContent>
      </Dialog>
  )
}
