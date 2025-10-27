"use client"

import type React from "react"

import { useState } from "react"
import { Header } from "@/components/header"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import Link from "next/link"
import { useRouter } from "next/navigation"
import { ImagePlus, X } from "lucide-react"
import apiClient from "@/lib/axios"

const emoticons = [
  { emoji: "😊", label: "행복해요" },
  { emoji: "😔", label: "우울해요" },
  { emoji: "😰", label: "불안해요" },
  { emoji: "😤", label: "화나요" },
  { emoji: "😌", label: "평온해요" },
  { emoji: "🥰", label: "감사해요" },
  { emoji: "😭", label: "힘들어요" },
  { emoji: "😐", label: "그저그래요" },
]

export default function NewCommunityPostPage() {
  const router = useRouter()
  const [selectedEmotion, setSelectedEmotion] = useState("")
  const [title, setTitle] = useState("")
  const [content, setContent] = useState("")
  const [images, setImages] = useState<string[]>([])
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async () => {
    if (!emoticons || !title.trim() || !content.trim()) {
      alert("감정, 제목, 내용을 모두 입력해주세요.")
      return
    }

    const formData = new FormData()
    formData.append("title", title)
    formData.append("content", content)
    formData.append("emotion", emoticons.find((e) => e.label === selectedEmotion)?.label || "")
    images.forEach((file) => formData.append("photos", file))

    setIsSubmitting(true)
    try {
      // ✅ 1️⃣ 게시글 작성 요청
      await apiClient.post("/posts", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      })

      // ✅ 2️⃣ 목록 새로고침 강제 (CommunityPage에서 fetchPosts가 실행되도록)
      router.push("/community?refresh=" + Date.now())

      alert("게시글이 성공적으로 등록되었습니다.")
    } catch (error) {
      console.error("게시글 작성 실패:", error)
      alert("게시글 작성 중 오류가 발생했습니다.")
    } finally {
      setIsSubmitting(false)
    }
  }


  const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files
    if (files) {
      const newImages = Array.from(files).map((file) => URL.createObjectURL(file))
      setImages([...images, ...newImages])
    }
  }

  const removeImage = (index: number) => {
    setImages(images.filter((_, i) => i !== index))
  }

  return (
    <div className="min-h-screen">
      <Header />

      <main className="container py-8 md:py-12 max-w-3xl">
        <div className="mb-8">
          <Link href="/community" className="text-sm text-muted-foreground hover:text-foreground">
            ← 커뮤니티로 돌아가기
          </Link>
        </div>

        <div className="space-y-6">
          <div>
            <h1 className="text-3xl font-bold mb-2">고민 나누기</h1>
            <p className="text-muted-foreground">익명으로 당신의 이야기를 들려주세요</p>
          </div>

          {/* Anonymous Notice */}
          <Card className="p-4 bg-secondary/50 border-secondary">
            <p className="text-sm leading-relaxed">
              🔒 이 글은 익명으로 작성됩니다. 랜덤 닉네임이 자동으로 부여되며, 개인정보는 노출되지 않습니다.
            </p>
          </Card>

          {/* Emotion Selection */}
          <Card className="p-6">
            <Label className="text-lg font-semibold mb-4 block">지금 기분이 어떠신가요?</Label>
            <div className="grid grid-cols-4 md:grid-cols-8 gap-3">
              {emoticons.map((emotion) => (
                <button
                  key={emotion.label}
                  onClick={() => setSelectedEmotion(emotion.label)}
                  className={`flex flex-col items-center gap-2 p-3 rounded-xl transition-all hover:scale-105 ${
                    selectedEmotion === emotion.label ? "ring-2 ring-primary bg-primary/10" : "hover:bg-muted"
                  }`}
                >
                  <span className="text-3xl">{emotion.emoji}</span>
                  <span className="text-xs text-center">{emotion.label}</span>
                </button>
              ))}
            </div>
          </Card>

          {/* Title */}
          <Card className="p-6">
            <Label htmlFor="title" className="text-lg font-semibold mb-4 block">
              제목
            </Label>
            <Input
              id="title"
              placeholder="제목을 입력하세요"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="text-base"
            />
          </Card>

          {/* Content */}
          <Card className="p-6">
            <Label htmlFor="content" className="text-lg font-semibold mb-4 block">
              내용
            </Label>
            <Textarea
              id="content"
              placeholder="자유롭게 고민을 나눠보세요. 이곳은 안전한 공간입니다."
              className="min-h-[300px] resize-none text-base leading-relaxed"
              value={content}
              onChange={(e) => setContent(e.target.value)}
            />
          </Card>

          {/* Image Upload */}
          <Card className="p-6">
            <Label className="text-lg font-semibold mb-4 block">사진 추가 (선택)</Label>
            <div className="space-y-4">
              {images.length > 0 && (
                <div className="grid grid-cols-3 gap-4">
                  {images.map((image, index) => (
                    <div key={index} className="relative aspect-square rounded-lg overflow-hidden bg-muted">
                      <img
                        src={image || "/placeholder.svg"}
                        alt={`Upload ${index + 1}`}
                        className="w-full h-full object-cover"
                      />
                      <button
                        onClick={() => removeImage(index)}
                        className="absolute top-2 right-2 p-1 bg-background/80 rounded-full hover:bg-background"
                      >
                        <X className="h-4 w-4" />
                      </button>
                    </div>
                  ))}
                </div>
              )}
              <label className="flex items-center justify-center gap-2 p-6 border-2 border-dashed rounded-lg cursor-pointer hover:bg-muted transition-colors">
                <ImagePlus className="h-5 w-5 text-muted-foreground" />
                <span className="text-sm text-muted-foreground">사진 추가하기</span>
                <input type="file" accept="image/*" multiple className="hidden" onChange={handleImageUpload} />
              </label>
            </div>
          </Card>

          {/* Guidelines */}
          <Card className="p-4 bg-accent/20 border-accent/30">
            <p className="text-sm text-muted-foreground leading-relaxed mb-2">
              <strong>커뮤니티 가이드라인</strong>
            </p>
            <ul className="text-sm text-muted-foreground space-y-1 list-disc list-inside">
              <li>타인을 존중하고 배려하는 언어를 사용해주세요</li>
              <li>개인정보나 민감한 정보는 공유하지 마세요</li>
              <li>악의적이거나 공격적인 내용은 신고될 수 있습니다</li>
            </ul>
          </Card>

          {/* Submit */}
          <div className="flex gap-4">
            <Button
              size="lg"
              className="flex-1"
              onClick={handleSubmit}
              disabled={!selectedEmotion || !title || !content}
            >
              작성 완료
            </Button>
            <Button size="lg" variant="outline" asChild>
              <Link href="/community">취소</Link>
              {isSubmitting ? "작성 중..." : "작성 완료"}
            </Button>
          </div>
        </div>
      </main>
    </div>
  )
}
