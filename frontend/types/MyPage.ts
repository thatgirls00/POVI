export interface TranscriptionDetail {
    transcriptionId: number;
    content: string;
    quoteContent: string;
    quoteAuthor: string;
    createdAt: string;
  }
  
  // 필사 목록 응답 (백엔드의 TranscriptionListRes)
  export interface TranscriptionListRes {
    transcriptionList: TranscriptionDetail[];
    totalCount: number;
  }
  
  // 프로필 정보 (백엔드의 ProfileRes)
  export interface ProfileRes {
    nickname: string;
    profileImgUrl: string;
    bio: string;
  }

  // 커뮤니티 글 목록 응답 아이템 (백엔드의 PostListResponse)
export interface CommunityPost {
    postId: number;
    title: string;
    content: string;
    authorNickname: string;
    createdAt: string;
    emoticon: string; // CommunityEmoticon은 string으로 처리
    likeCount: number;
    commentCount: number;
  }
  
  // 북마크 목록 응답 아이템 (백엔드의 BookmarkListResponse)
  export interface BookmarkedPost {
    postId: number;
    postTitle: string;
    content: string;
    postAuthorNickname: string;
    emoticon: string;
    postCreatedAt: string;
  }
  
  // 마이페이지 전체 응답 (백엔드의 MyPageRes)
  export interface MyPageRes {
    profileRes: ProfileRes;
    diaryCount: number;
    transcriptionListRes: TranscriptionListRes;
  }

  // 마이페이지 내 댓글 목록 아이템 (백엔드의 CommentListResponse)
export interface MyComment {
    commentId: number;
    content: string;
    createdAt: string;
    postId: number;
    postTitle: string;
  }
  
  // 좋아요 누른 글 목록 아이템 (백엔드의 LikeListResponse)
  export interface LikedPost {
    postId: number;
    postTitle: string;
    content: string;
    postAuthorNickname: string;
    emoticon: string;
    postCreatedAt: string;
  }

  // 댓글 응답 (백엔드의 CommentResponse)
export interface CommentResponse {
    // 백엔드 DTO에 따라 필요한 필드를 추가하세요. (예: commentId, content, authorNickname 등)
    commentId: number;
    content: string;
    authorNickname: string;
    createdAt: string;
  }
  
  // 게시글 상세 응답 (백엔드의 PostDetailResponse)
  export interface PostDetailResponse {
    postId: number;
    title: string;
    content: string;
    emoticon: string;
    authorNickname: string;
    createdAt: string;
    comments: CommentResponse[];
    photoUrls: string[];
  }