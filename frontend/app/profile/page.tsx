"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { Header } from "@/components/header"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { CommunityDetailDialog } from "@/components/community-detail-dialog"
import { MessageCircle, Sparkles, Bookmark, Settings, Heart, Upload, Trash2, Loader2, MessageSquare } from "lucide-react"
import Link from "next/link"
import { MyPageRes, TranscriptionDetail, CommunityPost, ProfileRes, BookmarkedPost, MyComment, LikedPost, PostDetailResponse, CommentResponse } from "@/types/MyPage"

const myDiaries = [
  {
    id: 1,
    date: "2025년 1월 15일",
    emotion: "😊",
    title: "좋은 하루였어요",
    preview: "오늘은 친구들과 즐거운 시간을 보냈어요...",
    content: "오늘은 친구들과 즐거운 시간을 보냈어요. 오랜만에 만나서 이야기를 나누니 마음이 따뜻해졌어요.",
    visibility: "private",
    hasImage: false,
    allowComments: false,
  },
]

const myCommunityPosts = [
  {
    id: 1,
    author: "익명의 토끼",
    date: "2시간 전",
    emotion: "😔",
    title: "요즘 너무 힘들어요",
    content: "일도 잘 안 풀리고 사람들과의 관계도 어려워요. 어떻게 해야 할지 모르겠어요.",
    likes: 24,
    comments: 8,
    hasImage: false,
    isMyPost: true,
  },
]

const myBookmarkedPosts = [
  {
    id: 1,
    author: "익명의 새",
    date: "5시간 전",
    emotion: "😊",
    title: "작은 행복을 찾았어요",
    content: "오늘 길을 걷다가 예쁜 꽃을 발견했어요. 작은 것에서 행복을 느낄 수 있다는 게 참 좋아요.",
    likes: 42,
    comments: 15,
    hasImage: true,
    isMyPost: false,
  },
]

const timeAgo = (dateString: string): string => {
  const now = new Date();
  const past = new Date(dateString);
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const pastDate = new Date(past.getFullYear(), past.getMonth(), past.getDate());
  const diffTime = today.getTime() - pastDate.getTime();
  const diffInDays = diffTime / (1000 * 60 * 60 * 24);

  if (diffInDays === 0) return "오늘";
  if (diffInDays === 1) return "1일 전";
  return `${diffInDays}일 전`;
};


export default function ProfilePage() {
  const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL;

  // --- 상태 관리 ---
  const [myPageData, setMyPageData] = useState<MyPageRes | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // 프로필 수정용 상태
  const [isEditing, setIsEditing] = useState(false);
  const [nickname, setNickname] = useState("");
  const [bio, setBio] = useState("");
  const [profileImage, setProfileImage] = useState("");
  const [imageFile, setImageFile] = useState<File | null>(null);

  // 필사 탭 페이지네이션 상태 추가
  const [transcriptions, setTranscriptions] = useState<TranscriptionDetail[]>([]);
  const [totalTranscriptions, setTotalTranscriptions] = useState(0);
  const [currentTranscriptionPage, setCurrentTranscriptionPage] = useState(0); // 현재 페이지 (0부터 시작)
  const transcriptionPageSize = 4; // 한 페이지에 보여줄 개수

  // 커뮤니티 탭 상태
  const [communityPosts, setCommunityPosts] = useState<CommunityPost[]>([]);
  const [totalCommunityPosts, setTotalCommunityPosts] = useState(0);
  const [currentCommunityPage, setCurrentCommunityPage] = useState(0);
  const [isCommunityLoading, setIsCommunityLoading] = useState(false);
  const communityPageSize = 4;

  // 북마크 탭 상태
  const [bookmarkedPosts, setBookmarkedPosts] = useState<BookmarkedPost[]>([]);
  const [totalBookmarkedPosts, setTotalBookmarkedPosts] = useState(0);
  const [currentBookmarkPage, setCurrentBookmarkPage] = useState(0);
  const [isBookmarkLoading, setIsBookmarkLoading] = useState(false);
  const bookmarkPageSize = 4;

  // 활동 탭 상태 수정
  const [myComments, setMyComments] = useState<MyComment[]>([]);
  const [totalComments, setTotalComments] = useState(0);
  const [currentCommentsPage, setCurrentCommentsPage] = useState(0);
  const [isCommentsLoading, setIsCommentsLoading] = useState(false);
  const commentsPageSize = 2;

  const [likedPosts, setLikedPosts] = useState<LikedPost[]>([]);
  const [totalLikedPosts, setTotalLikedPosts] = useState(0);
  const [currentLikesPage, setCurrentLikesPage] = useState(0);
  const [isLikesLoading, setIsLikesLoading] = useState(false);
  const likesPageSize = 2;

  // (다이얼로그 상태는 유지)
  const [selectedPost, setSelectedPost] = useState<any>(null);
  const [selectedPostDetail, setSelectedPostDetail] = useState<PostDetailResponse | null>(null);
  const [isDetailLoading, setIsDetailLoading] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);

  const emojiMap: Record<string, string> = {
    HAPPY: "😊",
    LOL: "😂",
    NERVOUS: "😰",
    NORMAL: "😐",
    SAD: "😢",
    CRYING: "😭",
    ANGRY: "😡",
    WORRIED: "😟",
  };
  // --- API 호출 ---
  useEffect(() => {
    const fetchMyPageData = async () => {
      setIsLoading(true);
      const token = localStorage.getItem("accessToken");
      if (!token) {
        // TODO: 로그인 페이지로 리디렉션
        setIsLoading(false);
        return;
      }

      try {
        const response = await fetch(`${baseUrl}/me/myPage`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!response.ok) throw new Error("Failed to fetch MyPage data");
        
        const data: MyPageRes = await response.json();
        console.log(data);
        setMyPageData(data);

        setTranscriptions(data.transcriptionListRes.transcriptionList);
        setTotalTranscriptions(data.transcriptionListRes.totalCount);
        
        // 수정용 상태를 API 데이터로 초기화
        setNickname(data.profileRes.nickname);
        setBio(data.profileRes.bio ?? "");
        setProfileImage(data.profileRes.profileImgUrl);

      } catch (error) {
        console.error(error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchMyPageData();
  }, [baseUrl]);

  const fetchTranscriptionsPage = async (page: number) => {
    const token = localStorage.getItem("accessToken");
    if (!token) return;

    try {
      const response = await fetch(`${baseUrl}/transcriptions/me?page=${page}&size=${transcriptionPageSize}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!response.ok) throw new Error("Failed to fetch transcriptions page");
      
      const data = await response.json();
      setTranscriptions(data.transcriptionList); // 목록을 새 페이지 데이터로 교체
      setTotalTranscriptions(data.totalCount); // 총 개수도 업데이트
    } catch (error) {
      console.error(error);
    }
  };

// 커뮤니티 글 목록 가져오기
const fetchCommunityPosts = async (page: number) => {
  const token = localStorage.getItem("accessToken");
  if (!token) return;
  try {
    const response = await fetch(`${baseUrl}/posts/me?page=${page}&size=${communityPageSize}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error("커뮤니티 글 로딩 실패");
    const data = await response.json();
    setCommunityPosts(data.content);
    setTotalCommunityPosts(data.totalElements);
  } catch (error) { console.error(error); }
};

const fetchBookmarkedPosts = async (page: number) => {
  const token = localStorage.getItem("accessToken");
  if (!token) return;
  try {
    const response = await fetch(`${baseUrl}/posts/me/bookmarks?page=${page}&size=${bookmarkPageSize}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error("북마크 글 로딩 실패");
    const data = await response.json();
    setBookmarkedPosts(data.content);
    setTotalBookmarkedPosts(data.totalElements);
  } catch (error) { console.error(error); }
};

const fetchMyComments = async (page: number) => {
  const token = localStorage.getItem("accessToken");
  if (!token) return;
  try {
    const response = await fetch(`${baseUrl}/posts/me/comments?page=${page}&size=${commentsPageSize}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error("내 댓글 로딩 실패");
    const data = await response.json();
    setMyComments(data.content);
    setTotalComments(data.totalElements);
  } catch (error) { console.error(error); }
};

const fetchMyLikedPosts = async (page: number) => {
  const token = localStorage.getItem("accessToken");
  if (!token) return;
  try {
    const response = await fetch(`${baseUrl}/posts/me/likes?page=${page}&size=${likesPageSize}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error("좋아요 누른 글 로딩 실패");
    const data = await response.json();
    setLikedPosts(data.content);
    setTotalLikedPosts(data.totalElements);
  } catch (error) { console.error(error); }
};

const fetchPostDetail = async (postId: number) => {
  setIsDetailLoading(true);
  const token = localStorage.getItem("accessToken");
  // 상세 보기는 토큰이 없어도 가능할 수 있으므로, 에러 처리만 합니다.
  if (!token) {
    console.error("Token not found");
    setIsDetailLoading(false);
    return;
  }

  try {
    const response = await fetch(`${baseUrl}/posts/${postId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error("게시글 상세 정보 로딩 실패");

    const data: PostDetailResponse = await response.json();
    setSelectedPostDetail(data);
  } catch (error) {
    console.error(error);
    // 에러 발생 시 다이얼로그를 닫거나 에러 메시지를 표시할 수 있습니다.
    setDialogOpen(false); 
  } finally {
    setIsDetailLoading(false);
  }
};

  // --- 핸들러 ---
  const handleEdit = () => {
    if (myPageData) {
      // 수정 모드 진입 시 현재 데이터로 폼을 채웁니다.
      setNickname(myPageData.profileRes.nickname);
      setBio(myPageData.profileRes.bio ?? "");
      setProfileImage(myPageData.profileRes.profileImgUrl);
      setIsEditing(true);
    }
  };

  const handleTranscriptionPageChange = (page: number) => {
    setCurrentTranscriptionPage(page);
    fetchTranscriptionsPage(page);
  };
  const handleCommunityPageChange = (page: number) => {
    setCurrentCommunityPage(page);
    fetchCommunityPosts(page);
  };
  const handleBookmarkPageChange = (page: number) => {
    setCurrentBookmarkPage(page);
    fetchBookmarkedPosts(page);
  };
  const handleCommentsPageChange = (page: number) => {
    setCurrentCommentsPage(page);
    fetchMyComments(page);
  };
  const handleLikesPageChange = (page: number) => {
    setCurrentLikesPage(page);
    fetchMyLikedPosts(page);
  };
  
  const openPostDetail = (postId: number) => {
    setDialogOpen(true); // 다이얼로그를 먼저 엽니다 (로딩 상태를 보여주기 위함).
    setSelectedPostDetail(null); // 이전 데이터를 초기화합니다.
    fetchPostDetail(postId); // 상세 데이터를 불러옵니다.
  };

  const handleTabChange = (tab: string) => {
    // '커뮤니티' 탭을 눌렀고, 아직 불러온 데이터가 없을 때만 API를 호출
    if (tab === "community" && communityPosts.length === 0) {
      fetchCommunityPosts(0);
    } 
    // '북마크' 탭을 눌렀고, 아직 불러온 데이터가 없을 때만 API를 호출
    else if (tab === "bookmarks" && bookmarkedPosts.length === 0) {
      fetchBookmarkedPosts(0);
    }
    else if (tab === "interactions" && myComments.length === 0) {
      // 활동 탭을 처음 누르면 댓글과 좋아요 목록을 모두 불러옵니다.
      fetchMyComments(0);
      fetchMyLikedPosts(0);
    }
  };

  const handleSave = async () => {
  const token = localStorage.getItem("accessToken");
  if (!token) {
    alert("로그인이 필요합니다.");
    return;
  }

  // 1. Create a FormData object
  const formData = new FormData();

  // 2. Create the DTO and append it as a JSON Blob.
  // This is crucial for Spring to read it with @RequestPart("dto")
  const profileUpdateReq = { nickname, bio };
  formData.append(
    "dto",
    new Blob([JSON.stringify(profileUpdateReq)], { type: "application/json" })
  );

  // 3. If a new image was selected, append the file.
  if (imageFile) {
    formData.append("image", imageFile);
  }

  try {
    const response = await fetch(`${baseUrl}/me/updateProfile`, {
      method: "PATCH",
      headers: {
        // IMPORTANT: Do NOT set 'Content-Type'.
        // The browser sets it automatically to 'multipart/form-data'
        // with the correct boundary when you use FormData.
        Authorization: `Bearer ${token}`,
      },
      body: formData,
    });

    if (!response.ok) {
      throw new Error("프로필 업데이트에 실패했습니다.");
    }

    const updatedProfile: ProfileRes = await response.json();

    // 4. Update the page state with the fresh data from the server
    if (myPageData) {
      setMyPageData({
        ...myPageData,
        profileRes: updatedProfile,
      });
    }

    // 5. Exit editing mode and clear the temporary file state
    setIsEditing(false);
    setImageFile(null);

  } catch (error) {
    console.error(error);
    alert("프로필 업데이트 중 오류가 발생했습니다.");
  }
};
  
  const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setProfileImage(reader.result as string);
        setImageFile(file);
      };
      reader.readAsDataURL(file);
    }
  };

  const openCommunityDetail = (post: any) => {
    setSelectedPost(post);
    setDialogOpen(true);
  };

  const handleDeleteCalligraphy = async (transcriptionId: number) => {
    // 1. 사용자에게 정말 삭제할 것인지 확인받습니다.
    if (!window.confirm("정말로 이 필사 기록을 삭제하시겠습니까?")) {
      return; // 사용자가 '취소'를 누르면 함수 종료
    }

    const token = localStorage.getItem("accessToken");
    if (!token) {
      alert("로그인이 필요합니다.");
      return;
    }

    try {
      // 2. 백엔드에 DELETE 요청을 보냅니다.
      const response = await fetch(`${baseUrl}/transcriptions/${transcriptionId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error("필사 기록 삭제에 실패했습니다.");
      }

      // 3. API 호출 성공 시, 화면(상태)에서 해당 항목을 즉시 제거합니다.
      setMyPageData(prevData => {
        if (!prevData) return null;

        // filter를 사용해 삭제할 ID를 제외한 새 배열을 만듭니다.
        const updatedList = prevData.transcriptionListRes.transcriptionList.filter(
          item => item.transcriptionId !== transcriptionId
        );

        // 기존 상태를 복사하고 필사 목록만 새 배열로 교체합니다.
        return {
          ...prevData,
          transcriptionListRes: {
            ...prevData.transcriptionListRes,
            transcriptionList: updatedList,
          },
        };
      });

    } catch (error) {
      console.error(error);
      alert("삭제 중 오류가 발생했습니다.");
    }
  };
  
  // --- 렌더링 ---
  if (isLoading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  if (!myPageData) {
    return <div>데이터를 불러오지 못했습니다.</div>;
  }

  // API로부터 받은 데이터
  const { profileRes, diaryCount, transcriptionListRes } = myPageData;
  const transcriptionTotalPages = Math.ceil(totalTranscriptions / transcriptionPageSize);
  const communityTotalPages = Math.ceil(totalCommunityPosts / communityPageSize);
  const bookmarkTotalPages = Math.ceil(totalBookmarkedPosts / bookmarkPageSize);
  const commentsTotalPages = Math.ceil(totalComments / commentsPageSize);
  const likesTotalPages = Math.ceil(totalLikedPosts / likesPageSize);

  // 페이지 버튼을 렌더링하는 재사용 가능한 컴포넌트
  const PaginationButtons = ({ totalPages, currentPage, onPageChange }: any) => {
    if (totalPages <= 1) return null;
    return (
      <div className="flex justify-center items-center gap-2 mt-6">
        {Array.from({ length: totalPages }, (_, i) => i).map((page) => (
          <Button
            key={page}
            variant={currentPage === page ? "default" : "outline"}
            size="sm"
            onClick={() => onPageChange(page)}
          >
            {page + 1}
          </Button>
        ))}
      </div>
    );
  };

  return (
    <div className="min-h-screen">
      <Header />

      <main className="container max-w-5xl py-8 md:py-12">
        {/* Profile Header */}
        <Card className="p-8 mb-6">
          <div className="flex flex-col md:flex-row gap-6 items-start md:items-center">
            <div className="relative">
              <Avatar className="h-24 w-24">
                <AvatarImage src={profileImage || "/placeholder.svg"} />
                <AvatarFallback className="text-2xl">{nickname.substring(0, 2)}</AvatarFallback>
              </Avatar>
              {isEditing && (
                <label
                  htmlFor="profile-image"
                  className="absolute bottom-0 right-0 p-1.5 bg-primary text-primary-foreground rounded-full cursor-pointer hover:bg-primary/90"
                >
                  <Upload className="h-3 w-3" />
                  <input id="profile-image" type="file" accept="image/*" className="hidden" onChange={handleImageUpload} />
                </label>
              )}
            </div>
            <div className="flex-1">
              {!isEditing ? (
                <>
                  <h1 className="text-2xl font-bold mb-2">{profileRes.nickname}</h1>
                  <p className="text-muted-foreground mb-4">{profileRes.bio}</p>
                  <Button size="sm" variant="outline" onClick={handleEdit}>
                    <Settings className="h-4 w-4 mr-2" />
                    프로필 수정
                  </Button>
                </>
              ) : (
                <div className="space-y-4">
                  <div>
                    <Label htmlFor="nickname">닉네임</Label>
                    <Input id="nickname" value={nickname} onChange={(e) => setNickname(e.target.value)} />
                  </div>
                  <div>
                    <Label htmlFor="bio">소개</Label>
                    <Textarea id="bio" value={bio} onChange={(e) => setBio(e.target.value)} rows={2} />
                  </div>
                  <div className="flex gap-2">
                    <Button size="sm" onClick={handleSave}>저장</Button>
                    <Button size="sm" variant="outline" onClick={() => setIsEditing(false)}>취소</Button>
                  </div>
                </div>
              )}
            </div>
            <div className="grid grid-cols-4 gap-4 text-center">
              <Link href="/diary-posts">
                <div className="cursor-pointer hover:opacity-80 transition-opacity">
                  <p className="text-2xl font-bold">{diaryCount}</p>
                  <p className="text-sm text-muted-foreground">다이어리</p>
                </div>
              </Link>
              <Link href="/profile/followers">
              <div className="cursor-pointer hover:opacity-80 transition-opacity">
                <p className="text-2xl font-bold">98</p>
                <p className="text-sm text-muted-foreground">팔로워</p>
              </div>
              </Link>

              <Link href="/profile/following">
              <div className="cursor-pointer hover:opacity-80 transition-opacity">
                <p className="text-2xl font-bold">131</p>
                <p className="text-sm text-muted-foreground">팔로잉</p>
              </div>
              </Link>
            </div>
          </div>
        </Card>

        <Tabs defaultValue="calligraphy" className="space-y-6" onValueChange={handleTabChange}>
          <TabsList className="grid w-full grid-cols-4">
              <TabsTrigger value="calligraphy">
                <Sparkles className="h-4 w-4 mr-2" />
                필사
              </TabsTrigger>
              <TabsTrigger value="community">
                <MessageCircle className="h-4 w-4 mr-2" />
                커뮤니티
              </TabsTrigger>
              <TabsTrigger value="bookmarks">
                <Bookmark className="h-4 w-4 mr-2" />
                북마크
              </TabsTrigger>
              <TabsTrigger value="interactions">
                <Heart className="h-4 w-4 mr-2" />
                활동
              </TabsTrigger>
            </TabsList>

          {/* Calligraphy Tab */}
          <TabsContent value="calligraphy" className="space-y-4">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-semibold">필사한 문장</h2>
              <p className="text-sm text-muted-foreground">총 {totalTranscriptions}개</p>
            </div>
            {transcriptions.length > 0 ? (
              <div className="grid md:grid-cols-2 gap-4">
                {transcriptions.map((item) => (
                  <Card key={item.transcriptionId} className="p-6 relative group">
                    <p className="text-base leading-relaxed mb-3">{item.content}</p>
                    <p className="text-xs text-muted-foreground">{timeAgo(item.createdAt)}</p>
                    <Button
                      size="sm"
                      variant="ghost"
                      className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity text-destructive hover:text-destructive"
                      onClick={() => handleDeleteCalligraphy(item.transcriptionId)}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </Card>
                ))}
              </div>
            ) : (
              <Card className="p-6 text-center text-muted-foreground">아직 필사한 문장이 없어요.</Card>
            )}
{/* 
              {transcriptionTotalPages > 1 && (
              <div className="flex justify-center items-center gap-2 mt-6">
                {Array.from({ length: transcriptionTotalPages }, (_, i) => i).map((page) => (
                  <Button
                    key={page}
                    variant={currentTranscriptionPage === page ? "default" : "outline"}
                    size="sm"
                    onClick={() => handleTranscriptionPageChange(page)}
                  >
                    {page + 1}
                  </Button>
                ))}
              </div>
            )} */}
            <PaginationButtons totalPages={transcriptionTotalPages} currentPage={currentTranscriptionPage} onPageChange={handleTranscriptionPageChange} />
          </TabsContent>

        {/* Community Tab */}
        <TabsContent value="community" className="space-y-4">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-semibold">내가 쓴 글</h2>
              <p className="text-sm text-muted-foreground">총 {totalCommunityPosts}개</p>
            </div>
            {isCommunityLoading ? <div className="flex justify-center p-8"><Loader2 className="h-8 w-8 animate-spin"/></div> :
              communityPosts.map((post) => (
                <Card key={post.postId} className="p-6 cursor-pointer hover:shadow-lg" onClick={() => openPostDetail(post.postId)}>
                  {/* --- 이모지 포함된 카드 내용 추가 --- */}
                  <div className="flex gap-4">
                    <div className="text-3xl">{emojiMap[post.emoticon] || '😐'}</div>
                    <div className="flex-1">
                      <h3 className="font-semibold mb-1">{post.title}</h3>
                      <p className="text-sm text-muted-foreground mb-2"> 익명 • {timeAgo(post.createdAt)}</p>
                      <p className="text-sm text-muted-foreground line-clamp-2">{post.content}</p>
                      <div className="flex items-center gap-4 mt-3">
                        <span className="text-sm text-muted-foreground">공감 {post.likeCount}</span>
                        <span className="text-sm text-muted-foreground">댓글 {post.commentCount}</span>
                      </div>
                    </div>
                  </div>
                </Card>
              ))
            }
            <PaginationButtons totalPages={communityTotalPages} currentPage={currentCommunityPage} onPageChange={handleCommunityPageChange} isLoading={isCommunityLoading} />
            </TabsContent>

          {/* Bookmarks Tab */}
          <TabsContent value="bookmarks" className="space-y-4">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-semibold">북마크한 글</h2>
              <p className="text-sm text-muted-foreground">총 {totalBookmarkedPosts}개</p>
            </div>
            {isBookmarkLoading ? <div className="flex justify-center p-8"><Loader2 className="h-8 w-8 animate-spin"/></div> :
              bookmarkedPosts.map((post) => (
                <Card key={post.postId} className="p-6 cursor-pointer hover:shadow-lg" onClick={() => openPostDetail(post.postId)}>
                  {/* --- 이모지 포함된 카드 내용 추가 --- */}
                  <div className="flex gap-4">
                    <div className="text-3xl">{emojiMap[post.emoticon] || '😐'}</div>
                    <div className="flex-1">
                      <h3 className="font-semibold mb-1">{post.postTitle}</h3>
                      <p className="text-sm text-muted-foreground mb-2"> 익명 • {timeAgo(post.postCreatedAt)}</p>
                      <p className="text-sm text-muted-foreground line-clamp-2">{post.content}</p>
                    </div>
                  </div>
                </Card>
              ))
            }
            <PaginationButtons totalPages={bookmarkTotalPages} currentPage={currentBookmarkPage} onPageChange={handleBookmarkPageChange} />
          </TabsContent>
          {/* Interactions Tab */}
          <TabsContent value="interactions" className="space-y-8">
            {/* 내가 쓴 댓글 섹션 */}
            <div>
              <h2 className="text-xl font-semibold mb-4">내가 쓴 댓글</h2>
              <div className="space-y-4">
                {myComments.map((comment) => (
                  <Card key={comment.commentId} className="p-6 cursor-pointer hover:shadow-lg transition-shadow" onClick={() => {openPostDetail(comment.postId)}}>
                    <div className="flex gap-4 items-start">
                      <MessageSquare className="h-5 w-5 text-muted-foreground flex-shrink-0 mt-1" />
                      <div className="flex-1">
                        <p className="text-sm mb-2">{comment.content}</p>
                        <p className="text-xs text-muted-foreground">
                          "{comment.postTitle}" 글에 작성 • {timeAgo(comment.createdAt)}
                        </p>
                      </div>
                    </div>
                  </Card>
                ))}
                <PaginationButtons totalPages={commentsTotalPages} currentPage={currentCommentsPage} onPageChange={handleCommentsPageChange} isLoading={isCommentsLoading} />
              </div>
            </div>
            <div>
              <h2 className="text-xl font-semibold mb-4">좋아요 누른 글</h2>
              <p className="text-sm text-muted-foreground -mt-4 mb-4">총 {totalLikedPosts}개</p>
              <div className="space-y-4">
                {isLikesLoading ? <div className="flex justify-center p-8"><Loader2 className="h-6 w-6 animate-spin"/></div> :
                  likedPosts.map((post) => (
                    <Card key={post.postId} className="p-6 cursor-pointer hover:shadow-lg transition-shadow" onClick={() => openPostDetail(post.postId)}>
                      {/* --- 🚨 이모지 포함된 카드 내용 추가 --- */}
                      <div className="flex gap-4">
                        <div className="text-3xl">{emojiMap[post.emoticon] || '😐'}</div>
                        <div className="flex-1">
                          <h3 className="font-semibold mb-1">{post.postTitle}</h3>
                          <p className="text-sm text-muted-foreground mb-2"> 익명 • {timeAgo(post.postCreatedAt)}</p>
                          <p className="text-sm text-muted-foreground line-clamp-2">{post.content}</p>
                        </div>
                      </div>
                    </Card>
                  ))
                }
                <PaginationButtons totalPages={likesTotalPages} currentPage={currentLikesPage} onPageChange={handleLikesPageChange} isLoading={isLikesLoading} />
              </div>
            </div>
          </TabsContent>
        </Tabs>

        {/* Dialogs */}
        {selectedPost && (
          <CommunityDetailDialog open={dialogOpen} onOpenChange={setDialogOpen} post={selectedPost} />
        )}
      </main>
    </div>
  )
}