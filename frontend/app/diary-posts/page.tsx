"use client";

import { JSX, useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { Header } from "@/components/header";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { ImageIcon, PenLine, Calendar, Heart } from "lucide-react";
import api from "@/lib/axios";
import DiaryDetailDialog from "@/components/diary-detail-dialog";

/* ===== 상수 ===== */
const PAGE_SIZE = 15; // 15개/페이지 고정

/* ===== 절대 URL ===== */
const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL ?? "";
const abs = (path?: string) =>
    !path ? "" : path.startsWith("http") ? path : `${API_BASE}${path}`;

/* ===== 타입 ===== */
type Visibility = "public" | "friend" | "private";
type MoodEmoji =
    | "HAPPY"
    | "SAD"
    | "ANGRY"
    | "NEUTRAL"
    | "EXCITED"
    | "TIRED"
    | "CALM"
    | string;

type MyDiaryCardVM = {
    id: number;
    title: string;
    preview: string;
    moodEmoji: MoodEmoji;
    emojiSymbol: string;
    thumbnailUrl?: string;
    visibility: Visibility;
    createdDate: string; // yyyy-MM-dd
    commentCount: number;
    liked: boolean;
    likeCount: number;
};

type MyDiaryListVM = {
    totalCount: number;
    thisWeekCount: number;
    averageMood: string;
    myDiaries: MyDiaryCardVM[];
};

type DiaryCardVM = {
    id: number;
    authorId: number;
    authorName: string;
    title: string;
    preview: string;
    moodEmoji: MoodEmoji;
    emojiSymbol: string;
    thumbnailUrl?: string;
    visibility: Visibility;
    createdDate: string; // yyyy-MM-dd
    commentCount: number;
    liked: boolean;
    likeCount: number;
};

/* ===== 이모지 매핑 ===== */
const emojiMap: Record<string, string> = {
    HAPPY: "😊",
    SAD: "😢",
    ANGRY: "😡",
    NEUTRAL: "😐",
    EXCITED: "🤩",
    TIRED: "😴",
    CALM: "😌",
};

/* ===== 변환 유틸 ===== */
const toVisibility = (v?: string): Visibility =>
    v === "PUBLIC" ? "public" : v === "FRIEND" ? "friend" : "private";

const toMyDiaryListVM = (data: any): MyDiaryListVM => ({
    totalCount: data?.totalCount ?? 0,
    thisWeekCount: data?.thisWeekCount ?? 0,
    averageMood: emojiMap[data?.moodSummary?.representative] ?? "🙂",
    myDiaries: (data?.myDiaries ?? []).map(
        (d: any): MyDiaryCardVM => ({
            id: d.postId,
            title: d.title,
            preview: d.preview,
            moodEmoji: d.moodEmoji,
            emojiSymbol: emojiMap[d.moodEmoji] ?? "🙂",
            thumbnailUrl: d.thumbnailUrl,
            visibility: toVisibility(d.visibility),
            createdDate: d.createdDate,
            commentCount: d.commentCount ?? 0,
            liked: !!d.liked,
            likeCount: d.likeCount ?? 0,
        })
    ),
});

const toDiaryCardVM = (d: any): DiaryCardVM => ({
    id: d.postId,
    authorId: d.authorId,
    authorName: d.authorName,
    title: d.title,
    preview: d.preview,
    moodEmoji: d.moodEmoji,
    emojiSymbol: emojiMap[d.moodEmoji] ?? "🙂",
    thumbnailUrl: d.thumbnailUrl,
    visibility: toVisibility(d.visibility),
    createdDate: d.createdDate,
    commentCount: d.commentCount ?? 0,
    liked: !!d.liked,
    likeCount: d.likeCount ?? 0,
});

/* ===== 페이지 컴포넌트 ===== */
export default function DiaryPostsPage(): JSX.Element {
    const router = useRouter();
    const search = useSearchParams();

    /* URL → 초기 상태 */
    const now = useMemo(() => new Date(), []);
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth() + 1;

    // 클램프: 오늘 이후 금지
    const clampYM = (y: number, m: number) => {
        if (y > currentYear) return { y: currentYear, m: currentMonth };
        if (y === currentYear && m > currentMonth) return { y, m: currentMonth };
        return { y, m };
    };

    const initYearRaw = Number(search.get("year") ?? currentYear);
    const initMonthRaw = Number(search.get("month") ?? currentMonth);
    const { y: initYear, m: initMonth } = clampYM(initYearRaw, initMonthRaw);
    const initPage = Number(search.get("page") ?? 0);

    const [tab, setTab] = useState<"my" | "friend" | "all">("my");
    const [year, setYear] = useState<number>(initYear);
    const [month, setMonth] = useState<number>(initMonth);
    const [page, setPage] = useState<number>(initPage);

    const [myData, setMyData] = useState<MyDiaryListVM | null>(null);
    const [friends, setFriends] = useState<DiaryCardVM[]>([]);
    const [explore, setExplore] = useState<DiaryCardVM[]>([]);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState<string | null>(null);

    /* 탭/쿼리 기반 데이터 로드 */
    useEffect(() => {
        let cancelled = false;

        const fetchMy = async () => {
            const q = new URLSearchParams();
            if (year) q.set("year", String(year));
            if (month) q.set("month", String(month));
            q.set("page", String(page));
            q.set("size", String(PAGE_SIZE));

            router.replace(`/diary-posts?${q.toString()}`);

            const { data } = await api.get(`/diary-posts/mine?${q.toString()}`);
            if (!cancelled) setMyData(toMyDiaryListVM(data));
        };

        const fetchFriends = async () => {
            const { data } = await api.get("/diary-posts/friends");
            if (!cancelled) setFriends((data ?? []).map(toDiaryCardVM));
        };

        const fetchExplore = async () => {
            const { data } = await api.get("/diary-posts/explore");
            if (!cancelled) setExplore((data ?? []).map(toDiaryCardVM));
        };

        const run = async () => {
            setLoading(true);
            setErr(null);
            try {
                if (tab === "my") await fetchMy();
                else if (tab === "friend" && friends.length === 0) await fetchFriends();
                else if (tab === "all" && explore.length === 0) await fetchExplore();
            } catch (e: any) {
                if (!cancelled) setErr(e?.response?.data?.message ?? "불러오기 실패");
            } finally {
                if (!cancelled) setLoading(false);
            }
        };

        run();
        return () => {
            cancelled = true;
        };
    }, [tab, year, month, page]);

    const visibilityLabel = (v: Visibility) =>
        v === "private" ? "비공개" : v === "friend" ? "친구공개" : "전체공개";

    /* 좋아요 토글 (낙관적 → 서버값 보정) */
    const toggleLike = async (postId: number) => {
        const apply = (arr: any[], setArr: any) =>
            setArr(
                arr.map((it) =>
                    it.id === postId
                        ? {
                            ...it,
                            liked: !it.liked,
                            likeCount: it.liked ? it.likeCount - 1 : it.likeCount + 1,
                        }
                        : it
                )
            );

        try {
            if (tab === "my" && myData) {
                const updated = myData.myDiaries.map((it) =>
                    it.id === postId
                        ? {
                            ...it,
                            liked: !it.liked,
                            likeCount: it.liked ? it.likeCount - 1 : it.likeCount + 1,
                        }
                        : it
                );
                setMyData({ ...myData, myDiaries: updated });
            } else if (tab === "friend") apply(friends, setFriends);
            else apply(explore, setExplore);

            const { data } = await api.post(`/diary-posts/${postId}/likes/toggle`);
            const reconcile = (arr: any[], setArr: any) =>
                setArr(
                    arr.map((it) =>
                        it.id === postId
                            ? { ...it, liked: !!data.liked, likeCount: data.likeCount ?? it.likeCount }
                            : it
                    )
                );

            if (tab === "my" && myData)
                reconcile(myData.myDiaries, (next: MyDiaryCardVM[]) =>
                    setMyData({ ...myData, myDiaries: next })
                );
            else if (tab === "friend") reconcile(friends, setFriends);
            else reconcile(explore, setExplore);
        } catch {
            alert("좋아요 처리 실패");
        }
    };

    /* 상세 모달 */
    const [selectedDiary, setSelectedDiary] = useState<any | null>(null);
    const [openDetail, setOpenDetail] = useState(false);

    const handleOpenDiary = async (id: number) => {
        try {
            const { data } = await api.get(`/diary-posts/${id}`);
            const dateStr = data?.createdAt ? new Date(data.createdAt).toISOString().slice(0, 10) : "";
            setSelectedDiary({
                id: data.postId,
                title: data.title,
                content: data.content,
                emotion: emojiMap[data.moodEmoji] ?? "🙂",
                visibility: toVisibility(data.visibility),
                date: dateStr,
                hasImage: Array.isArray(data.imageUrls) && data.imageUrls.length > 0,
                imageUrls: (data.imageUrls ?? []).map((u: string) => abs(u)),
                liked: !!data.liked,
                likeCount: data.likeCount ?? 0,
                commentCount: data.commentCount ?? 0,
                isMine: tab === "my" || !!data.isMine,
            });
            setOpenDetail(true);
        } catch (e: any) {
            alert(e?.response?.data?.message ?? "다이어리 불러오기 실패");
        }
    };

    /* 이번 주 판별 (삭제 시 thisWeekCount 보정) */
    const isInThisWeek = (isoDate?: string): boolean => {
        if (!isoDate) return false;
        const d = new Date(isoDate);
        const today = new Date();
        const today0 = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        const dow = (today0.getDay() + 6) % 7;
        const start = new Date(today0);
        start.setDate(today0.getDate() - dow);
        const end = new Date(start);
        end.setDate(start.getDate() + 7);
        return d >= start && d < end;
    };

    /* 삭제 반영 */
    const handlePostDeleted = (postId: number, createdDate?: string) => {
        setOpenDetail(false);
        setSelectedDiary(null);

        setFriends((prev) => prev.filter((d) => d.id !== postId));
        setExplore((prev) => prev.filter((d) => d.id !== postId));
        setMyData((prev) =>
            prev
                ? {
                    ...prev,
                    totalCount: Math.max(0, prev.totalCount - 1),
                    thisWeekCount: Math.max(0, prev.thisWeekCount - (isInThisWeek(createdDate) ? 1 : 0)),
                    myDiaries: prev.myDiaries.filter((d) => d.id !== postId),
                }
                : prev
        );
        setPage((p) => p); // 현재 페이지 재조회 트리거
    };

    /* 공통 카드 푸터 */
    const renderCardFooter = (d: any) => (
        <div className="mt-3 flex items-center gap-4 text-sm text-muted-foreground">
            <button
                className="inline-flex items-center gap-1 hover:opacity-80"
                onClick={(e) => {
                    e.stopPropagation();
                    toggleLike(d.id);
                }}
            >
                <Heart className={`h-4 w-4 ${d.liked ? "fill-current" : ""}`} />
                <span>{d.likeCount}</span>
            </button>
            <span>💬 {d.commentCount}</span>
        </div>
    );

    const isLastPage = (page + 1) * PAGE_SIZE >= (myData?.totalCount ?? 0);

    /* ===== 렌더 ===== */
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
                            <Link href="/diary-posts/new">
                                <PenLine className="h-4 w-4 mr-2" />
                                새 다이어리
                            </Link>
                        </Button>
                    </div>
                </div>

                <Tabs value={tab} onValueChange={(v) => setTab(v as any)} className="space-y-6">
                    <TabsList className="grid w-full max-w-md grid-cols-3">
                        <TabsTrigger value="my">나의 다이어리</TabsTrigger>
                        <TabsTrigger value="friend">친구 다이어리</TabsTrigger>
                        <TabsTrigger value="all">다이어리 둘러보기</TabsTrigger>
                    </TabsList>

                    {/* ===== 나의 다이어리 ===== */}
                    <TabsContent value="my" className="space-y-6">
                        {loading && !myData ? <p>불러오는 중…</p> : err && !myData ? <p>{err}</p> : null}
                        {myData && (
                            <>
                                {/* 요약 카드 */}
                                <div className="grid md:grid-cols-3 gap-4">
                                    <Card className="p-6">
                                        <p className="text-sm text-muted-foreground mb-1">총 작성 개수</p>
                                        <p className="text-2xl font-bold">{myData.totalCount}개</p>
                                    </Card>
                                    <Card className="p-6">
                                        <p className="text-sm text-muted-foreground mb-1">이번 주 기록</p>
                                        <p className="text-2xl font-bold">{myData.thisWeekCount}개</p>
                                    </Card>
                                    <Card className="p-6">
                                        <p className="text-sm text-muted-foreground mb-1">평균 감정</p>
                                        <p className="text-2xl font-bold">{myData.averageMood}</p>
                                    </Card>
                                </div>

                                {/* 연/월 + 페이지 컨트롤 */}
                                <div className="flex flex-wrap items-center gap-3">
                                    <select
                                        className="border rounded-md px-3 py-2"
                                        value={year}
                                        onChange={(e) => {
                                            const y = Number(e.target.value);
                                            const maxMonth = y === currentYear ? currentMonth : 12;
                                            const nextMonth = Math.min(month, maxMonth);
                                            setPage(0);
                                            setYear(y);
                                            setMonth(nextMonth);
                                        }}
                                    >
                                        {Array.from({ length: 6 }).map((_, i) => {
                                            const y = currentYear - i;
                                            return (
                                                <option key={y} value={y}>
                                                    {y}년
                                                </option>
                                            );
                                        })}
                                    </select>

                                    {(() => {
                                        const maxMonthForYear = year === currentYear ? currentMonth : 12;
                                        return (
                                            <select
                                                className="border rounded-md px-3 py-2"
                                                value={month}
                                                onChange={(e) => {
                                                    const m = Number(e.target.value);
                                                    setPage(0);
                                                    setMonth(Math.min(m, maxMonthForYear));
                                                }}
                                            >
                                                {Array.from({ length: maxMonthForYear }).map((_, i) => {
                                                    const m = i + 1;
                                                    return (
                                                        <option key={m} value={m}>
                                                            {m}월
                                                        </option>
                                                    );
                                                })}
                                            </select>
                                        );
                                    })()}

                                    <div className="ml-auto flex items-center gap-2">
                                        <button
                                            className="px-3 py-2 border rounded-md disabled:opacity-40"
                                            disabled={page <= 0}
                                            onClick={() => setPage((p) => Math.max(0, p - 1))}
                                        >
                                            이전
                                        </button>
                                        <span className="text-sm">page {page + 1}</span>
                                        <button
                                            className="px-3 py-2 border rounded-md"
                                            onClick={() => setPage((p) => p + 1)}
                                            disabled={isLastPage}
                                        >
                                            다음
                                        </button>
                                        <span className="ml-2 text-sm text-muted-foreground hidden sm:inline">
                      {PAGE_SIZE}개/페이지
                    </span>
                                    </div>
                                </div>

                                {/* 카드 리스트 */}
                                <div className="space-y-4">
                                    {myData.myDiaries.map((d) => (
                                        <Card
                                            key={d.id}
                                            className="p-6 hover:shadow-lg transition-shadow cursor-pointer"
                                            onClick={() => handleOpenDiary(d.id)}
                                        >
                                            <div className="flex gap-4">
                                                <div className="text-4xl">{d.emojiSymbol}</div>
                                                <div className="flex-1">
                                                    <div className="flex items-start justify-between mb-2">
                                                        <div>
                                                            <h3 className="font-semibold text-lg mb-1">{d.title}</h3>
                                                            <p className="text-sm text-muted-foreground">{d.createdDate}</p>
                                                        </div>
                                                        <span className="text-xs px-2 py-1 bg-muted rounded-full">
                              {visibilityLabel(d.visibility)}
                            </span>
                                                    </div>
                                                    <p className="text-muted-foreground leading-relaxed">{d.preview}</p>
                                                    {renderCardFooter(d)}
                                                </div>
                                                {d.thumbnailUrl ? (
                                                    <img
                                                        src={abs(d.thumbnailUrl)}
                                                        alt="thumb"
                                                        className="w-20 h-20 rounded-lg object-cover"
                                                    />
                                                ) : (
                                                    <div className="flex-shrink-0 w-20 h-20 bg-muted rounded-lg flex items-center justify-center">
                                                        <ImageIcon className="h-8 w-8 text-muted-foreground" />
                                                    </div>
                                                )}
                                            </div>
                                        </Card>
                                    ))}
                                </div>
                            </>
                        )}
                    </TabsContent>

                    {/* ===== 친구 다이어리 ===== */}
                    <TabsContent value="friend" className="space-y-4">
                        {friends.map((d) => (
                            <Card
                                key={d.id}
                                className="p-6 hover:shadow-lg transition-shadow cursor-pointer"
                                onClick={() => handleOpenDiary(d.id)}
                            >
                                <div className="flex gap-4">
                                    <div className="text-4xl">{d.emojiSymbol}</div>
                                    <div className="flex-1">
                                        <h3 className="font-semibold text-lg mb-1">{d.title}</h3>
                                        <p className="text-sm text-muted-foreground">
                                            {d.authorName} • {d.createdDate}
                                        </p>
                                        <p className="text-muted-foreground leading-relaxed">{d.preview}</p>
                                        {renderCardFooter(d)}
                                    </div>
                                </div>
                            </Card>
                        ))}
                    </TabsContent>

                    {/* ===== 모두의 다이어리 ===== */}
                    <TabsContent value="all" className="space-y-4">
                        {explore.map((d) => (
                            <Card
                                key={d.id}
                                className="p-6 hover:shadow-lg transition-shadow cursor-pointer"
                                onClick={() => handleOpenDiary(d.id)}
                            >
                                <div className="flex gap-4">
                                    <div className="text-4xl">{d.emojiSymbol}</div>
                                    <div className="flex-1">
                                        <div className="flex items-start justify-between mb-2">
                                            <div>
                                                <h3 className="font-semibold text-lg mb-1">{d.title}</h3>
                                                <p className="text-sm text-muted-foreground">
                                                    {d.authorName} • {d.createdDate}
                                                </p>
                                            </div>
                                        </div>
                                        <p className="text-muted-foreground leading-relaxed">{d.preview}</p>
                                        {renderCardFooter(d)}
                                    </div>
                                </div>
                            </Card>
                        ))}
                    </TabsContent>
                </Tabs>
            </main>

            {selectedDiary && (
                <DiaryDetailDialog
                    open={openDetail}
                    onOpenChange={setOpenDetail}
                    diary={selectedDiary}
                    onLikeToggle={(postId, liked, likeCount) => {
                        const updateList = (list: DiaryCardVM[] | MyDiaryCardVM[], setter: any) =>
                            setter(list.map((it: any) => (it.id === postId ? { ...it, liked, likeCount } : it)));

                        if (tab === "my" && myData) {
                            const next = myData.myDiaries.map((it) =>
                                it.id === postId ? { ...it, liked, likeCount } : it
                            );
                            setMyData({ ...myData, myDiaries: next });
                        } else if (tab === "friend") {
                            updateList(friends, setFriends);
                        } else {
                            updateList(explore, setExplore);
                        }
                    }}
                    onCommentChange={(postId, commentCount) => {
                        const updateComments = (list: DiaryCardVM[] | MyDiaryCardVM[], setter: any) =>
                            setter(list.map((it: any) => (it.id === postId ? { ...it, commentCount } : it)));

                        if (myData) {
                            const next = myData.myDiaries.map((it) =>
                                it.id === postId ? { ...it, commentCount } : it
                            );
                            setMyData({ ...myData, myDiaries: next });
                        }
                        updateComments(friends, setFriends);
                        updateComments(explore, setExplore);
                    }}
                    onPostDeleted={handlePostDeleted}
                />
            )}
        </div>
    );
}
