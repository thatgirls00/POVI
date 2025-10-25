"use client";

import {JSX, useEffect, useState} from "react";
import Link from "next/link";
import {Header} from "@/components/header";
import {Button} from "@/components/ui/button";
import {Card} from "@/components/ui/card";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs";
import {ImageIcon, PenLine, Calendar, Heart} from "lucide-react";
import api from "@/lib/axios";
import {DiaryDetailDialog} from "@/components/diary-detail-dialog";

// 절대 URL 변환
const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL ?? "";
const abs = (path?: string) =>
    !path ? "" : path.startsWith("http") ? path : `${API_BASE}${path}`;

// ===== 타입 정의 =====
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
    createdDate: string;
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
    createdDate: string;
    commentCount: number;
    liked: boolean;
    likeCount: number;
};

// ===== 이모지 매핑 =====
const emojiMap: Record<string, string> = {
    HAPPY: "😊",
    SAD: "😢",
    ANGRY: "😡",
    NEUTRAL: "😐",
    EXCITED: "🤩",
    TIRED: "😴",
    CALM: "😌",
};

// ===== 변환 유틸 =====
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

// ===== 페이지 컴포넌트 =====
export default function DiaryPostsPage(): JSX.Element {
    const [tab, setTab] = useState<"my" | "friend" | "all">("my");
    const [myData, setMyData] = useState<MyDiaryListVM | null>(null);
    const [friends, setFriends] = useState<DiaryCardVM[]>([]);
    const [explore, setExplore] = useState<DiaryCardVM[]>([]);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState<string | null>(null);

    // ===== 데이터 로드 =====
    useEffect(() => {
        let cancelled = false;
        const fetchTab = async () => {
            setLoading(true);
            setErr(null);
            try {
                if (tab === "my" && !myData) {
                    const {data} = await api.get("/diary-posts/mine");
                    if (!cancelled) setMyData(toMyDiaryListVM(data));
                } else if (tab === "friend" && friends.length === 0) {
                    const {data} = await api.get("/diary-posts/friends");
                    if (!cancelled) setFriends((data ?? []).map(toDiaryCardVM));
                } else if (tab === "all" && explore.length === 0) {
                    const {data} = await api.get("/diary-posts/explore");
                    if (!cancelled) setExplore((data ?? []).map(toDiaryCardVM));
                }
            } catch (e: any) {
                if (!cancelled) setErr(e?.response?.data?.message ?? "불러오기 실패");
            } finally {
                if (!cancelled) setLoading(false);
            }
        };
        fetchTab();
        return () => {
            cancelled = true;
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [tab]);

    const visibilityLabel = (v: Visibility) =>
        v === "private" ? "비공개" : v === "friend" ? "친구공개" : "전체공개";

    // ===== 좋아요 토글 =====
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
                setMyData({...myData, myDiaries: updated});
            } else if (tab === "friend") apply(friends, setFriends);
            else apply(explore, setExplore);

            const {data} = await api.post(`/diary-posts/${postId}/likes/toggle`);
            const reconcile = (arr: any[], setArr: any) =>
                setArr(
                    arr.map((it) =>
                        it.id === postId
                            ? {...it, liked: !!data.liked, likeCount: data.likeCount ?? it.likeCount}
                            : it
                    )
                );

            if (tab === "my" && myData)
                reconcile(myData.myDiaries, (next: MyDiaryCardVM[]) =>
                    setMyData({...myData, myDiaries: next})
                );
            else if (tab === "friend") reconcile(friends, setFriends);
            else reconcile(explore, setExplore);
        } catch {
            alert("좋아요 처리 실패");
        }
    };

    // ===== 상세 모달 =====
    const [selectedDiary, setSelectedDiary] = useState<any | null>(null);
    const [openDetail, setOpenDetail] = useState(false);

    const handleOpenDiary = async (id: number) => {
        try {
            const {data} = await api.get(`/diary-posts/${id}`);
            const dateStr = data?.createdAt
                ? new Date(data.createdAt).toISOString().slice(0, 10)
                : "";
            setSelectedDiary({
                id: data.postId,
                title: data.title,
                content: data.content,
                emotion: emojiMap[data.moodEmoji] ?? "🙂",
                visibility: toVisibility(data.visibility),
                date: dateStr,
                hasImage:
                    Array.isArray(data.imageUrls) && data.imageUrls.length > 0,
                imageUrls: (data.imageUrls ?? []).map((u: string) => abs(u)),
                liked: !!data.liked,
                likeCount: data.likeCount ?? 0,
                commentCount: data.commentCount ?? 0,
            });
            setOpenDetail(true);
        } catch (e: any) {
            alert(e?.response?.data?.message ?? "다이어리 불러오기 실패");
        }
    };

    // ===== 카드 공통 렌더러 =====
    const renderCardFooter = (d: any) => (
        <div className="mt-3 flex items-center gap-4 text-sm text-muted-foreground">
            <button
                className="inline-flex items-center gap-1 hover:opacity-80"
                onClick={(e) => {
                    e.stopPropagation();
                    toggleLike(d.id);
                }}
            >
                <Heart className={`h-4 w-4 ${d.liked ? "fill-current" : ""}`}/>
                <span>{d.likeCount}</span>
            </button>
            <span>💬 {d.commentCount}</span>
        </div>
    );

    // ===== 렌더 =====
    return (
        <div className="min-h-screen">
            <Header/>
            <main className="container py-8 md:py-12 max-w-5xl">
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
                    <div>
                        <h1 className="text-3xl font-bold mb-2">다이어리</h1>
                        <p className="text-muted-foreground">감정의 기록들을 돌아보세요</p>
                    </div>
                    <div className="flex gap-3">
                        <Button variant="outline" asChild>
                            <Link href="/calendar">
                                <Calendar className="h-4 w-4 mr-2"/>
                                캘린더 보기
                            </Link>
                        </Button>
                        <Button asChild>
                            <Link href="/diary-posts/new">
                                <PenLine className="h-4 w-4 mr-2"/>
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

                    {/* 나의 다이어리 */}
                    <TabsContent value="my" className="space-y-6">
                        {loading && !myData ? <p>불러오는 중…</p> : err && !myData ? <p>{err}</p> : null}
                        {myData && (
                            <>
                                <div className="grid md:grid-cols-3 gap-4">
                                    <Card className="p-6">
                                        <p className="text-sm text-muted-foreground mb-1">총 작성 일수</p>
                                        <p className="text-2xl font-bold">{myData.totalCount}일</p>
                                    </Card>
                                    <Card className="p-6">
                                        <p className="text-sm text-muted-foreground mb-1">이번 주 기록</p>
                                        <p className="text-2xl font-bold">{myData.thisWeekCount}일</p>
                                    </Card>
                                    <Card className="p-6">
                                        <p className="text-sm text-muted-foreground mb-1">평균 감정</p>
                                        <p className="text-2xl font-bold">{myData.averageMood}</p>
                                    </Card>
                                </div>

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
                                                    <div
                                                        className="flex-shrink-0 w-20 h-20 bg-muted rounded-lg flex items-center justify-center">
                                                        <ImageIcon className="h-8 w-8 text-muted-foreground"/>
                                                    </div>
                                                )}
                                            </div>
                                        </Card>
                                    ))}
                                </div>
                            </>
                        )}
                    </TabsContent>

                    {/* 친구 다이어리 */}
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

                    {/* 모두의 다이어리 */}
                    <TabsContent value="all" className="space-y-4">
                        {explore.map((d: DiaryCardVM) => (
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
                        // 좋아요 반영
                        const updateList = (list: DiaryCardVM[] | MyDiaryCardVM[], setter: any) =>
                            setter(list.map((it: any) => it.id === postId ? { ...it, liked, likeCount } : it));

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
                        // 댓글 수 반영
                        const updateComments = (list: DiaryCardVM[] | MyDiaryCardVM[], setter: any) =>
                            setter(list.map((it: any) => it.id === postId ? { ...it, commentCount } : it));

                        if (myData) {
                            const next = myData.myDiaries.map((it) =>
                                it.id === postId ? { ...it, commentCount } : it
                            );
                            setMyData({ ...myData, myDiaries: next });
                        }
                        updateComments(friends, setFriends);
                        updateComments(explore, setExplore);
                    }}
                />
            )}
        </div>
    );
}
