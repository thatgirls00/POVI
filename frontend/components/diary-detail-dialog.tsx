"use client"

import {JSX, useEffect, useRef, useState} from "react"
import {Dialog, DialogContent, DialogHeader, DialogTitle} from "@/components/ui/dialog"
import {Button} from "@/components/ui/button"
import {Card} from "@/components/ui/card"
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar"
import {Textarea} from "@/components/ui/textarea"
import {
    Heart,
    MessageCircle,
    Pencil,
    Trash2,
    Maximize2,
    ChevronLeft,
    ChevronRight,
} from "lucide-react";
import api from "@/lib/axios";

interface DiaryDetailDialogProps {
    open: boolean
    onOpenChange: (open: boolean) => void
    diary: {
        id: number
        author?: string
        date: string
        emotion: string
        title: string
        content: string
        visibility?: string
        hasImage?: boolean
        likes?: number
        allowComments?: boolean
        imageUrls?: string[];
    };
    onLikeToggle?: (postId: number, liked: boolean, likeCount: number) => void;
    onCommentChange?: (postId: number, commentCount: number) => void;
};

interface Comment {
    id: number
    authorName: string
    content: string
    createdAt: string
    isMine: boolean
}

export function DiaryDetailDialog({
                                      open,
                                      onOpenChange,
                                      diary,
                                      onLikeToggle,
                                      onCommentChange,
                                  }: DiaryDetailDialogProps): JSX.Element {
    const [comment, setComment] = useState("")
    const [comments, setComments] = useState<Comment[]>([])
    const [isLiked, setIsLiked] = useState(false)
    const [likeCount, setLikeCount] = useState(0);
    const [likeLoading, setLikeLoading] = useState(false);
    const [editingCommentId, setEditingCommentId] = useState<number | null>(null)
    const [editCommentText, setEditCommentText] = useState("")

    const [zoomIdx, setZoomIdx] = useState<number | null>(null);
    const images = Array.isArray(diary.imageUrls) ? diary.imageUrls : [];

    type LikeRes = { liked: boolean; likeCount: number };

    // 1) 댓글 가져오기
    useEffect(() => {
        if (!open || !diary?.id) return;

        const fetchComments = async () => {
            try {
                const res = await api.get(`/diary-posts/${diary.id}/comments`, {
                    params: {size: 30, sort: "id,asc"},
                });
                const list = Array.isArray(res.data.content) ? res.data.content : [];
                setComments(
                    list.map((c: any) => ({
                        id: c.commentId,
                        authorName: c.authorName,
                        content: c.content,
                        createdAt: new Date(c.createdAt).toLocaleString(),
                        isMine: c.isMine,
                    }))
                );
            } catch (err) {
                console.error("❌ 댓글 조회 실패:", err);
                setComments([]);
            }
        };

        fetchComments();
    }, [open, diary?.id]);

    // 2) 댓글 수 변경을 부모에 통지(렌더 이후)
    const prevCountRef = useRef<number>(0);
    useEffect(() => {
        if (!open) return;
        if (comments.length !== prevCountRef.current) {
            onCommentChange?.(diary.id, comments.length);
            prevCountRef.current = comments.length;
        }
    }, [open, comments.length, diary.id, onCommentChange]);


// 추가: 좋아요 상태/카운트 불러오기
    useEffect(() => {
        if (!open || !diary?.id) return;

        const fetchLikes = async () => {
            try {
                const {data} = await api.get(`/diary-posts/${diary.id}/likes/me`);
                // { liked: boolean, likeCount: number }
                setIsLiked(Boolean(data.liked));
                setLikeCount(Number(data.likeCount ?? 0));
            } catch (err) {
                console.error("❌ 좋아요 상태 조회 실패:", err);
                setIsLiked(false);
                setLikeCount(0);
            }
        };
        fetchLikes();
    }, [open, diary?.id]);

// 추가: 좋아요 토글
    const handleToggleLike = async () => {
        if (!diary?.id || likeLoading) return;
        setLikeLoading(true);
        try {
            const {data} = await api.post<LikeRes>(`/diary-posts/${diary.id}/likes/toggle`);
            // { liked: boolean, likeCount: number }
            setIsLiked(Boolean(data.liked));
            setLikeCount(Number(data.likeCount ?? 0));

            // ✅ 부모 콜백 호출 (리스트 갱신)
            onLikeToggle?.(diary.id, Boolean(data.liked), Number(data.likeCount ?? 0));

        } catch (err) {
            console.error("❌ 좋아요 토글 실패:", err);
        } finally {
            setLikeLoading(false);
        }
    };
// 댓글 작성
    const handleCreateComment = async () => {
        if (!comment.trim()) return;
        try {
            const res = await api.post(`/diary-posts/${diary.id}/comments`, {content: comment.trim()});
            setComments(prev => [
                ...prev,
                {
                    id: res.data.commentId,
                    authorName: res.data.authorName,
                    content: res.data.content,
                    createdAt: new Date(res.data.createdAt).toLocaleString(),
                    isMine: true,
                },
            ]);
            setComment("");
        } catch (err) {
            console.error("❌ 댓글 작성 실패:", err);
        }
    };

// ✅ 댓글 수정
    const handleSaveComment = async (commentId: number) => {
        try {
            await api.patch(`/diary-posts/${diary.id}/comments/${commentId}`, {content: editCommentText.trim()})
            setComments((prev) =>
                prev.map((c) => (c.id === commentId ? {...c, content: editCommentText} : c))
            )
            setEditingCommentId(null)
            setEditCommentText("")
        } catch (err) {
            console.error("❌ 댓글 수정 실패:", err)
        }
    }

// ✅ 댓글 삭제
    const handleDeleteComment = async (commentId: number) => {
        try {
            await api.delete(`/diary-posts/${diary.id}/comments/${commentId}`);
            setComments(prev => prev.filter(c => c.id !== commentId));
        } catch (e) {
            console.error("❌ 댓글 삭제 실패:", e);
        }
    };

    const prevImg = () => setZoomIdx((i) => (i === null ? null : (i + images.length - 1) % images.length));
    const nextImg = () => setZoomIdx((i) => (i === null ? null : (i + 1) % images.length));


    return (
        <>
            {/* 상세 모달 */}
            <Dialog open={open} onOpenChange={onOpenChange}>
                <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto" aria-describedby={undefined}>
                    <DialogHeader>
                        <DialogTitle className="sr-only">다이어리 상세보기</DialogTitle>
                    </DialogHeader>

                    <div className="space-y-6">
                        {/* -------------------- Header -------------------- */}
                        <div className="flex items-start gap-4">
                            <div className="text-5xl">{diary.emotion}</div>
                            <div className="flex-1">
                                <div className="flex items-start justify-between mb-2">
                                    <div>
                                        <h2 className="text-2xl font-bold mb-2">{diary.title}</h2>
                                        <div className="flex items-center gap-2 text-sm text-muted-foreground">
                                            {diary.author && (
                                                <>
                                                    <span>{diary.author}</span>
                                                    <span>•</span>
                                                </>
                                            )}
                                            <span>{diary.date}</span>
                                            {diary.visibility && (
                                                <>
                                                    <span>•</span>
                                                    <span className="px-2 py-0.5 bg-muted rounded-full text-xs">
                          {diary.visibility === "private"
                              ? "비공개"
                              : diary.visibility === "friends"
                                  ? "친구공개"
                                  : "전체공개"}
                        </span>
                                                </>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* -------------------- Content -------------------- */}
                        <div className="prose prose-sm max-w-none">
                            <p className="text-base leading-relaxed whitespace-pre-wrap">{diary.content}</p>
                        </div>

                        {/* -------------------- Image -------------------- */}
                        {/* ✅ 이미지 섹션: 썸네일 그리드 */}
                        {images.length > 0 && (
                            <div className="space-y-3">
                                <div className="grid grid-cols-2 md:grid-cols-3 gap-2">
                                    {images.map((src, idx) => (
                                        <button
                                            key={src + idx}
                                            type="button"
                                            className="relative group aspect-[4/3] overflow-hidden rounded-xl bg-muted"
                                            onClick={() => setZoomIdx(idx)}
                                        >
                                            <img
                                                src={src}
                                                alt={`이미지 ${idx + 1}`}
                                                className="h-full w-full object-cover transition-transform group-hover:scale-105"
                                                loading="lazy"
                                            />
                                            <span
                                                className="absolute right-2 top-2 rounded-md bg-black/50 px-1.5 py-0.5 text-xs text-white">
                      {idx + 1}/{images.length}
                    </span>
                                            <span className="absolute bottom-2 right-2 rounded-md bg-white/80 p-1">
                      <Maximize2 className="h-4 w-4"/>
                    </span>
                                        </button>
                                    ))}
                                </div>
                            </div>
                        )}

                        {/* -------------------- Actions -------------------- */}
                        <div className="flex items-center gap-2 pt-4 border-t">
                            <Button
                                variant="ghost"
                                size="sm"
                                className="gap-2"
                                onClick={handleToggleLike}
                                disabled={likeLoading}
                            >
                                <Heart className={`h-4 w-4 ${isLiked ? "fill-red-500 text-red-500" : ""}`}/>
                                {likeCount} {/* ✅ 서버 응답값 사용 */}
                            </Button>
                            <Button variant="ghost" size="sm" className="gap-2">
                                <MessageCircle className="h-4 w-4"/>
                                {comments.length}
                            </Button>
                        </div>


                        {/* -------------------- 댓글 섹션 -------------------- */}
                        {diary.allowComments !== false && (
                            <div className="space-y-4 pt-4 border-t">
                                <h3 className="font-semibold">댓글 {comments.length}</h3>

                                {/* 댓글 입력 */}
                                <div className="flex gap-3">
                                    <Avatar className="h-8 w-8">
                                        <AvatarImage src="/placeholder.svg"/>
                                        <AvatarFallback>나</AvatarFallback>
                                    </Avatar>
                                    <div className="flex-1 space-y-2">
                                        <Textarea
                                            placeholder="댓글을 입력하세요..."
                                            value={comment}
                                            onChange={(e) => setComment(e.target.value)}
                                            rows={2}
                                        />
                                        <Button size="sm" onClick={handleCreateComment} disabled={!comment.trim()}>
                                            댓글 작성
                                        </Button>
                                    </div>
                                </div>

                                {/* 댓글 목록 */}
                                <div className="space-y-4">
                                    {comments.map((c) => (
                                        <Card key={c.id} className="p-4">
                                            <div className="flex gap-3">
                                                <Avatar className="h-8 w-8">
                                                    <AvatarImage src="/placeholder.svg"/>
                                                    <AvatarFallback>{c.authorName[0]}</AvatarFallback>
                                                </Avatar>
                                                <div className="flex-1">
                                                    <div className="flex items-center justify-between mb-1">
                                                        <div className="flex items-center gap-2">
                                                            <span className="font-medium text-sm">{c.authorName}</span>
                                                            <span
                                                                className="text-xs text-muted-foreground">{c.createdAt}</span>
                                                        </div>
                                                        {c.isMine && (
                                                            <div className="flex gap-1">
                                                                <Button
                                                                    size="sm"
                                                                    variant="ghost"
                                                                    className="h-6 w-6 p-0"
                                                                    onClick={() => {
                                                                        setEditingCommentId(c.id)
                                                                        setEditCommentText(c.content)
                                                                    }}
                                                                >
                                                                    <Pencil className="h-3 w-3"/>
                                                                </Button>
                                                                <Button
                                                                    size="sm"
                                                                    variant="ghost"
                                                                    className="h-6 w-6 p-0 text-destructive hover:text-destructive"
                                                                    onClick={() => handleDeleteComment(c.id)}
                                                                >
                                                                    <Trash2 className="h-3 w-3"/>
                                                                </Button>
                                                            </div>
                                                        )}
                                                    </div>

                                                    {editingCommentId === c.id ? (
                                                        <div className="space-y-2">
                                                            <Textarea
                                                                value={editCommentText}
                                                                onChange={(e) => setEditCommentText(e.target.value)}
                                                                rows={2}
                                                            />
                                                            <div className="flex gap-2">
                                                                <Button size="sm"
                                                                        onClick={() => handleSaveComment(c.id)}>
                                                                    저장
                                                                </Button>
                                                                <Button
                                                                    size="sm"
                                                                    variant="outline"
                                                                    onClick={() => setEditingCommentId(null)}
                                                                >
                                                                    취소
                                                                </Button>
                                                            </div>
                                                        </div>
                                                    ) : (
                                                        <p className="text-sm leading-relaxed">{c.content}</p>
                                                    )}
                                                </div>
                                            </div>
                                        </Card>
                                    ))}
                                </div>
                            </div>
                        )}
                    </div>
                </DialogContent>
            </Dialog>

            {/* 확대 모달 */}
            <Dialog
                open={zoomIdx !== null}
                onOpenChange={(o) => {
                    if (!o) setZoomIdx(null);
                }}
            >
                <DialogContent className="max-w-6xl p-0" aria-describedby={undefined}>
                    <DialogHeader>
                        <DialogTitle className="sr-only">이미지 확대</DialogTitle>
                    </DialogHeader>

                    {zoomIdx !== null ? (
                        <div className="relative h-[80vh] w-full bg-black">
                            <img
                                src={images[zoomIdx] ?? ""}
                                alt={`확대 이미지 ${zoomIdx + 1}`}
                                className="mx-auto h-full object-contain"
                            />

                            {images.length > 1 ? (
                                <>
                                    <button
                                        onClick={() =>
                                            setZoomIdx((i) =>
                                                i === null ? null : (i + images.length - 1) % images.length
                                            )
                                        }
                                        className="absolute left-2 top-1/2 -translate-y-1/2 rounded-full bg-white/80 p-2"
                                    >
                                        <ChevronLeft className="h-6 w-6"/>
                                    </button>
                                    <button
                                        onClick={() =>
                                            setZoomIdx((i) => (i === null ? null : (i + 1) % images.length))
                                        }
                                        className="absolute right-2 top-1/2 -translate-y-1/2 rounded-full bg-white/80 p-2"
                                    >
                                        <ChevronRight className="h-6 w-6"/>
                                    </button>
                                </>
                            ) : null}

                            <div
                                className="absolute bottom-3 left-1/2 -translate-x-1/2 rounded-full bg-white/80 px-3 py-1 text-sm">
                                {zoomIdx + 1} / {images.length}
                            </div>
                        </div>
                    ) : null}
                </DialogContent>
            </Dialog>
        </>
    );

}