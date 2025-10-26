"use client"

import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Textarea } from "@/components/ui/textarea"
import { Heart, MessageCircle, Share2, Bookmark, Flag, ImageIcon, Pencil, Trash2, Maximize2 } from "lucide-react"
import { useState } from "react"

interface CommunityDetailDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  post: {
    id: number
    author: string
    date: string
    emoticon: string
    title: string
    content: string
    likes: number
    comments?: number
    hasImage?: boolean
    isMyPost?: boolean
  }
}

export function CommunityDetailDialog({ open, onOpenChange, post }: CommunityDetailDialogProps) {
  const [comment, setComment] = useState("")
  const [isLiked, setIsLiked] = useState(false)
  const [isBookmarked, setIsBookmarked] = useState(false)
  const [imageZoomed, setImageZoomed] = useState(false)
  const [editingCommentId, setEditingCommentId] = useState<number | null>(null)
  const [editCommentText, setEditCommentText] = useState("")

  const comments = [
    {
      id: 1,
      author: "익명의 나무",
      content: "저도 비슷한 경험이 있어요. 혼자가 아니에요. 함께 이겨내요!",
      time: "1시간 전",
      likes: 8,
      isMyComment: false,
    },
    {
      id: 2,
      author: "익명의 바다",
      content: "힘든 시기는 누구에게나 있어요. 천천히 한 걸음씩 나아가면 괜찮아질 거예요.",
      time: "1시간 전",
      likes: 12,
      isMyComment: true,
    },
  ]

  const handleEdit = () => {
    // Edit logic
    console.log("[v0] Edit post:", post.id)
  }

  const handleDelete = () => {
    // Delete logic
    console.log("[v0] Delete post:", post.id)
    onOpenChange(false)
  }

  const handleEditComment = (commentId: number, currentText: string) => {
    setEditingCommentId(commentId)
    setEditCommentText(currentText)
  }

  const handleSaveComment = (commentId: number) => {
    console.log("[v0] Save comment:", commentId, editCommentText)
    setEditingCommentId(null)
    setEditCommentText("")
  }

  const handleDeleteComment = (commentId: number) => {
    console.log("[v0] Delete comment:", commentId)
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="sr-only">커뮤니티 글 상세보기</DialogTitle>
        </DialogHeader>

        <div className="space-y-6">
          {/* Header */}
          <div className="flex items-start gap-4">
            <div className="text-5xl">{post.emoticon}</div>
            <div className="flex-1">
              <div className="flex items-start justify-between mb-2">
                <div>
                  <h2 className="text-2xl font-bold mb-2">{post.title}</h2>
                  <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <span>{post.author}</span>
                    <span>•</span>
                    <span>{post.date}</span>
                  </div>
                </div>
                <div className="flex gap-2">
                  {post.isMyPost && (
                    <>
                      <Button size="sm" variant="ghost" onClick={handleEdit}>
                        <Pencil className="h-4 w-4" />
                      </Button>
                      <Button
                        size="sm"
                        variant="ghost"
                        className="text-destructive hover:text-destructive"
                        onClick={handleDelete}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </>
                  )}
                  <Button size="sm" variant="ghost" className="text-muted-foreground hover:text-destructive">
                    <Flag className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </div>
          </div>

          {/* Content */}
          <div className="prose prose-sm max-w-none">
            <p className="text-base leading-relaxed whitespace-pre-wrap">{post.content}</p>
          </div>

          {/* Image if exists */}
          {post.hasImage && (
            <div className="relative w-full h-64 bg-muted rounded-lg flex items-center justify-center group">
              <ImageIcon className="h-16 w-16 text-muted-foreground" />
              <Button
                size="sm"
                variant="secondary"
                className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity"
                onClick={() => setImageZoomed(true)}
              >
                <Maximize2 className="h-4 w-4" />
              </Button>
            </div>
          )}

          {/* Actions */}
          <div className="flex items-center gap-2 pt-4 border-t">
            <Button variant="ghost" size="sm" className="gap-2" onClick={() => setIsLiked(!isLiked)}>
              <Heart className={`h-4 w-4 ${isLiked ? "fill-red-500 text-red-500" : ""}`} />
              {post.likes}
            </Button>
            <Button variant="ghost" size="sm" className="gap-2">
              <MessageCircle className="h-4 w-4" />
              {comments.length}
            </Button>
            <Button variant="ghost" size="sm" className="gap-2" onClick={() => setIsBookmarked(!isBookmarked)}>
              <Bookmark className={`h-4 w-4 ${isBookmarked ? "fill-current" : ""}`} />
            </Button>
            <Button variant="ghost" size="sm" className="gap-2 ml-auto">
              <Share2 className="h-4 w-4" />
            </Button>
          </div>

          {/* Comments Section */}
          <div className="space-y-4 pt-4 border-t">
            <h3 className="font-semibold">댓글 {comments.length}</h3>

            {/* Comment Input */}
            <div className="flex gap-3">
              <Avatar className="h-8 w-8">
                <AvatarImage src="/placeholder.svg" />
                <AvatarFallback>나</AvatarFallback>
              </Avatar>
              <div className="flex-1 space-y-2">
                <Textarea
                  placeholder="따뜻한 위로의 말을 남겨주세요..."
                  value={comment}
                  onChange={(e) => setComment(e.target.value)}
                  rows={2}
                />
                <Button size="sm" disabled={!comment.trim()}>
                  댓글 작성
                </Button>
              </div>
            </div>

            {/* Comments List */}
            <div className="space-y-4">
              {comments.map((c) => (
                <Card key={c.id} className="p-4">
                  <div className="flex gap-3">
                    <Avatar className="h-8 w-8">
                      <AvatarImage src="/placeholder.svg" />
                      <AvatarFallback>{c.author[0]}</AvatarFallback>
                    </Avatar>
                    <div className="flex-1">
                      <div className="flex items-center justify-between mb-1">
                        <div className="flex items-center gap-2">
                          <span className="font-medium text-sm">{c.author}</span>
                          <span className="text-xs text-muted-foreground">{c.time}</span>
                        </div>
                        <div className="flex gap-1">
                          {c.isMyComment && (
                            <>
                              <Button
                                size="sm"
                                variant="ghost"
                                className="h-6 w-6 p-0"
                                onClick={() => handleEditComment(c.id, c.content)}
                              >
                                <Pencil className="h-3 w-3" />
                              </Button>
                              <Button
                                size="sm"
                                variant="ghost"
                                className="h-6 w-6 p-0 text-destructive hover:text-destructive"
                                onClick={() => handleDeleteComment(c.id)}
                              >
                                <Trash2 className="h-3 w-3" />
                              </Button>
                            </>
                          )}
                          <Button
                            size="sm"
                            variant="ghost"
                            className="h-6 w-6 p-0 text-muted-foreground hover:text-destructive"
                          >
                            <Flag className="h-3 w-3" />
                          </Button>
                        </div>
                      </div>
                      {editingCommentId === c.id ? (
                        <div className="space-y-2">
                          <Textarea
                            value={editCommentText}
                            onChange={(e) => setEditCommentText(e.target.value)}
                            rows={2}
                          />
                          <div className="flex gap-2">
                            <Button size="sm" onClick={() => handleSaveComment(c.id)}>
                              저장
                            </Button>
                            <Button size="sm" variant="outline" onClick={() => setEditingCommentId(null)}>
                              취소
                            </Button>
                          </div>
                        </div>
                      ) : (
                        <>
                          <p className="text-sm leading-relaxed mb-2">{c.content}</p>
                          <button className="flex items-center gap-1 text-xs text-muted-foreground hover:text-primary">
                            <Heart className="h-3 w-3" />
                            {c.likes}
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                </Card>
              ))}
            </div>
          </div>
        </div>
      </DialogContent>

      {imageZoomed && (
        <Dialog open={imageZoomed} onOpenChange={setImageZoomed}>
          <DialogContent className="max-w-7xl max-h-[95vh] p-0">
            <div className="relative w-full h-[90vh] bg-black flex items-center justify-center">
              <ImageIcon className="h-32 w-32 text-muted-foreground" />
            </div>
          </DialogContent>
        </Dialog>
      )}
    </Dialog>
  )
}
