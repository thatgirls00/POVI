"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Header } from "@/components/header";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Upload, X } from "lucide-react";
import api from "@/lib/axios";

const toAbsUrl = (u: string) =>
    u.startsWith("http") ? u
        : `${process.env.NEXT_PUBLIC_API_BASE_URL}${u.startsWith("/") ? "" : "/"}${u}`;

const toServerPath = (u: string) => {
    try { return new URL(u).pathname; } catch { return u; }
};

type MoodOption = { code: string; label: string };

const TITLE_MIN = 2;
const TITLE_MAX = 50;
const CONTENT_MIN = 2;
const CONTENT_MAX = 3000;
const IMG_MAX = 3;


export default function NewDiaryPage() {
    const router = useRouter();

    const [moods, setMoods] = useState<MoodOption[]>([]);
    const [selectedMoodCode, setSelectedMoodCode] = useState("");
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [images, setImages] = useState<string[]>([]);
    const [visibility, setVisibility] =
        useState<"public" | "friend" | "private">("private");
    const [submitting, setSubmitting] = useState(false);
    const [uploading, setUploading] = useState(false);

    const fileInputRef = useRef<HTMLInputElement | null>(null);

    const canSubmit = useMemo(
        () =>
            !submitting &&
            selectedMoodCode.length > 0 &&
            title.trim().length >= TITLE_MIN &&
            content.trim().length >= CONTENT_MIN,
        [submitting, selectedMoodCode, title, content]
    );

    const splitEmoji = (label: string) => {
        const [emoji, text] = (label ?? "").trim().split(/\s+/, 2);
        return { emoji: emoji || "🙂", text: text ?? label };
    };

    useEffect(() => {
        (async () => {
            try {
                const { data } = await api.get<MoodOption[]>("/meta/moods");
                setMoods(Array.isArray(data) ? data : []);
            } catch {
                setMoods([]);
            }
        })();
    }, []);

    const openPicker = () => fileInputRef.current?.click();

    const handleFiles = async (files: FileList | null | undefined): Promise<void> => {
        if (!files || files.length === 0) return;

        const remain = IMG_MAX - images.length;
        const slice = Array.from(files).slice(0, Math.max(0, remain));
        if (slice.length === 0) return;

        const fd = new FormData();
        slice.forEach((f) => fd.append("images", f));

        try {
            setUploading(true);
            const { data } = await api.post<string[]>("/diary-images", fd, {
                headers: { "Content-Type": "multipart/form-data" },
            });
            setImages((prev) => [...prev, ...data.map(toAbsUrl)].slice(0, IMG_MAX));
        } catch {
            alert("이미지 업로드 실패");
        } finally {
            setUploading(false);
            fileInputRef.current && (fileInputRef.current.value = ""); // null 안전
        }
    };

    const removeImage = async (url: string) => {
        setImages((prev) => prev.filter((u) => u !== url));
        try {
            await api.delete("/diary-images", { params: { imageUrl: toServerPath(url) } });
        } catch {
            /* 서버 삭제 실패는 무시 */
        }
    };

    const handleSubmit = async () => {
        if (!canSubmit) {
            alert("감정, 제목, 내용을 최소 2자 이상 입력해주세요.");
            return;
        }
        setSubmitting(true);
        try {
            const payload = {
                title: title.trim(),
                content,
                moodEmoji: selectedMoodCode,
                visibility:
                    visibility === "friend" ? "FRIEND" : visibility.toUpperCase(),
                imageUrls: images,
            };
            await api.post("/diary-posts", payload);
            alert("다이어리가 생성되었습니다.");
            router.push("/diary-posts");
        } catch {
            alert("생성에 실패했습니다.");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="min-h-screen">
            <Header />
            <main className="container py-8 md:py-12 max-w-4xl">
                <div className="mb-8">
                    <Link href="/" className="text-sm text-muted-foreground hover:text-foreground">
                        ← 홈으로 돌아가기
                    </Link>
                </div>

                <div className="space-y-8">
                    <div>
                        <h1 className="text-3xl font-bold mb-2">오늘의 다이어리</h1>
                        <p className="text-muted-foreground">당신의 감정을 자유롭게 표현해보세요</p>
                    </div>

                    {/* 감정 선택 */}
                    <Card className="p-6">
                        <Label className="text-lg font-semibold mb-4 block">지금 기분이 어떠신가요?</Label>
                        <div className="grid grid-cols-4 md:grid-cols-8 gap-3">
                            {moods.length === 0 ? (
                                <p className="text-sm text-muted-foreground col-span-full">
                                    감정 데이터를 불러오는 중...
                                </p>
                            ) : (
                                moods.map((m) => {
                                    const { emoji, text } = splitEmoji(m.label);
                                    const selected = selectedMoodCode === m.code;
                                    return (
                                        <button
                                            key={m.code}
                                            type="button"
                                            onClick={() => setSelectedMoodCode(m.code)}
                                            className={`flex flex-col items-center gap-2 p-3 rounded-xl transition-all hover:scale-105 ${
                                                selected ? "ring-2 ring-primary bg-primary/10" : "hover:bg-muted"
                                            }`}
                                        >
                                            <span className="text-3xl">{emoji}</span>
                                            <span className="text-xs text-center">{text}</span>
                                        </button>
                                    );
                                })
                            )}
                        </div>
                    </Card>

                    {/* 제목 */}
                    <Card className="p-6">
                        <Label htmlFor="title" className="text-lg font-semibold mb-4 block">제목</Label>
                        <Input
                            id="title"
                            placeholder={`${
                                TITLE_MIN
                            }~${TITLE_MAX}자`}
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            maxLength={TITLE_MAX}
                        />
                    </Card>

                    {/* 내용 */}
                    <Card className="p-6">
                        <Label htmlFor="content" className="text-lg font-semibold mb-4 block">오늘 하루는 어땠나요?</Label>
                        <Textarea
                            id="content"
                            placeholder="자유롭게 감정을 표현해보세요..."
                            className="min-h-[300px] resize-none text-base leading-relaxed bg-background/50"
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                            maxLength={CONTENT_MAX}
                        />
                    </Card>

                    {/* 이미지 업로드 */}
                    <Card className="p-6">
                        <Label className="text-lg font-semibold mb-4 block">사진 첨부 (최대 {IMG_MAX}장)</Label>

                        {/* 숨김 파일 입력 */}
                        <input
                            ref={(el) => {
                                fileInputRef.current = el;
                            }}
                            type="file"
                            accept="image/*"
                            multiple
                            className="hidden"
                            onChange={(e) => handleFiles(e.target.files)}
                        />

                        <div className="grid grid-cols-3 gap-4">
                            {images.map((img, idx) => (
                                <div key={idx} className="relative aspect-square rounded-lg overflow-hidden bg-muted">
                                    <img src={img || "/placeholder.svg"} alt={`Upload ${idx + 1}`} className="w-full h-full object-cover" />
                                    <button
                                        type="button"
                                        onClick={() => removeImage(img)}
                                        className="absolute top-2 right-2 p-1 bg-destructive text-destructive-foreground rounded-full hover:bg-destructive/90"
                                    >
                                        <X className="h-4 w-4" />
                                    </button>
                                </div>
                            ))}

                            {images.length < IMG_MAX && (
                                <button
                                    type="button"
                                    onClick={openPicker}
                                    disabled={uploading}
                                    className="aspect-square rounded-lg border-2 border-dashed border-muted-foreground/25 hover:border-muted-foreground/50 flex flex-col items-center justify-center gap-2 transition-colors"
                                >
                                    <Upload className="h-6 w-6 text-muted-foreground" />
                                    <span className="text-xs text-muted-foreground">
                    {uploading ? "업로드 중..." : "사진 추가"}
                  </span>
                                </button>
                            )}
                        </div>
                    </Card>

                    {/* 공개 범위 */}
                    <Card className="p-6">
                        <Label className="text-lg font-semibold mb-4 block">공개 범위</Label>
                        <RadioGroup value={visibility} onValueChange={(v) => setVisibility(v as "public" | "friend" | "private")}>
                            <div className="flex items-center space-x-2">
                                <RadioGroupItem value="public" id="public" />
                                <Label htmlFor="public" className="font-normal cursor-pointer">전체 공개</Label>
                            </div>
                            <div className="flex items-center space-x-2">
                                <RadioGroupItem value="friend" id="friend" />
                                <Label htmlFor="friend" className="font-normal cursor-pointer">친구 공개</Label>
                            </div>
                            <div className="flex items-center space-x-2">
                                <RadioGroupItem value="private" id="private" />
                                <Label htmlFor="private" className="font-normal cursor-pointer">비공개</Label>
                            </div>
                        </RadioGroup>
                    </Card>

                    {/* 작성/취소 */}
                    <div className="flex gap-4">
                        <Button size="lg" className="flex-1" onClick={handleSubmit} disabled={!canSubmit}>
                            {submitting ? "작성 중..." : "작성 완료"}
                        </Button>
                        <Button size="lg" variant="outline" asChild>
                            <Link href="/">취소</Link>
                        </Button>
                    </div>
                </div>
            </main>
        </div>
    );
}
