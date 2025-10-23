"use client";

import {useEffect, useState} from "react";
import Link from "next/link";
import {Header} from "@/components/header";
import {Button} from "@/components/ui/button";
import {Card} from "@/components/ui/card";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs";
import {ImageIcon, PenLine, Calendar} from "lucide-react";
import api from "@/lib/axios";

// ====== (1) 이 파일 안에서 쓸 간단 타입/어댑터 ======
type Visibility = "public" | "friend" | "private";
type MoodEmoji =
    | "HAPPY" | "SAD" | "ANGRY" | "NEUTRAL" | "EXCITED" | "TIRED" | "CALM"
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
};

type MyDiaryListVM = {
  totalCount: number;
  thisWeekCount: number;
  averageMood: string;     // UI 표기용(이모지)
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
};

const emojiMap: Record<string, string> = {
  HAPPY: "😊",
  SAD: "😢",
  ANGRY: "😡",
  NEUTRAL: "😐",
  EXCITED: "🤩",
  TIRED: "😴",
  CALM: "😌",
};

const toVisibility = (v?: string): Visibility =>
    v === "PUBLIC" ? "public" : v === "FRIEND" ? "friend" : "private";

const toMyDiaryListVM = (data: any): MyDiaryListVM => ({
  totalCount: data?.totalCount ?? 0,
  thisWeekCount: data?.thisWeekCount ?? 0,
  averageMood: emojiMap[data?.moodSummary?.representative] ?? "🙂",
  myDiaries: (data?.myDiaries ?? []).map((d: any): MyDiaryCardVM => ({
    id: d.postId,
    title: d.title,
    preview: d.preview,
    moodEmoji: d.moodEmoji,
    emojiSymbol: emojiMap[d.moodEmoji] ?? "🙂",
    thumbnailUrl: d.thumbnailUrl,
    visibility: toVisibility(d.visibility),
    createdDate: d.createdDate,
    commentCount: d.commentCount ?? 0,
  })),
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
});

// ====== (2) 페이지 컴포넌트 ======
export default function DiaryPostsPage() {
  const [tab, setTab] = useState<"my" | "friend" | "all">("my");

  const [myData, setMyData] = useState<MyDiaryListVM | null>(null);
  const [friends, setFriends] = useState<DiaryCardVM[]>([]);
  const [explore, setExplore] = useState<DiaryCardVM[]>([]);

  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState<string | null>(null);

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
  }, [tab]); // eslint-disable-line react-hooks/exhaustive-deps

  const visibilityLabel = (v: Visibility) =>
      v === "private" ? "비공개" : v === "friend" ? "친구공개" : "전체공개";

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
              <>
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
                        <>
                          {myData.myDiaries.map((d) => (
                              <Card key={d.id} className="p-6 hover:shadow-lg transition-shadow">
                                <div className="flex gap-4">
                                  <div className="text-4xl">{d.emojiSymbol}</div>
                                  <div className="flex-1">
                                    <div className="flex items-start justify-between mb-2">
                                      <div>
                                        <h3 className="font-semibold text-lg mb-1">{d.title}</h3>
                                        <p className="text-sm text-muted-foreground">{d.createdDate}</p>
                                      </div>
                                      <span
                                          className="text-xs px-2 py-1 bg-muted rounded-full">
                              {visibilityLabel(d.visibility)}
                            </span>
                                    </div>
                                    <p className="text-muted-foreground leading-relaxed">{d.preview}</p>
                                    <div className="mt-3 text-sm text-muted-foreground">💬 {d.commentCount}</div>
                                  </div>
                                  {d.thumbnailUrl ? (
                                      <img src={d.thumbnailUrl} alt="thumb"
                                           className="w-20 h-20 rounded-lg object-cover"/>
                                  ) : (
                                      <div
                                          className="flex-shrink-0 w-20 h-20 bg-muted rounded-lg flex items-center justify-center">
                                        <ImageIcon className="h-8 w-8 text-muted-foreground"/>
                                      </div>
                                  )}
                                </div>

                              </Card>
                          ))}
                        </>
                      </div>
                    </>
                )}
              </>
            </TabsContent>

            <TabsContent value="friend" className="space-y-4">
              <>
                {loading && friends.length === 0 ? <p>불러오는 중…</p> : err && friends.length === 0 ?
                    <p>{err}</p> : null}

                <>
                  {friends.map((d) => (
                      <Card key={d.id} className="p-6 hover:shadow-lg transition-shadow">
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
                            <div className="mt-3 text-sm text-muted-foreground">💬 {d.commentCount}</div>
                          </div>
                          {d.thumbnailUrl ? (
                              <img src={d.thumbnailUrl} alt="thumb"
                                   className="w-20 h-20 rounded-lg object-cover"/>
                          ) : (
                              <div
                                  className="flex-shrink-0 w-20 h-20 bg-muted rounded-lg flex items-center justify-center">
                                <ImageIcon className="h-8 w-8 text-muted-foreground"/>
                              </div>
                          )}
                        </div>
                      </Card>
                  ))}
                </>
              </>
            </TabsContent>

            {/* 모두의 다이어리 */}
            <TabsContent value="all" className="space-y-4">
              <>
                {loading && explore.length === 0 ? <p>불러오는 중…</p> : err && explore.length === 0 ?
                    <p>{err}</p> : null}

                <>
                  {explore.map((d) => (
                      <Card key={d.id} className="p-6 hover:shadow-lg transition-shadow">
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
                            <div className="mt-3 text-sm text-muted-foreground">💬 {d.commentCount}</div>
                          </div>
                          {d.thumbnailUrl ? (
                              <img src={d.thumbnailUrl} alt="thumb"
                                   className="w-20 h-20 rounded-lg object-cover"/>
                          ) : (
                              <div
                                  className="flex-shrink-0 w-20 h-20 bg-muted rounded-lg flex items-center justify-center">
                                <ImageIcon className="h-8 w-8 text-muted-foreground"/>
                              </div>
                          )}
                        </div>
                      </Card>
                  ))}
                </>
              </>
            </TabsContent>
          </Tabs>
        </main>
      </div>
  );
}
