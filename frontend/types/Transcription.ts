export interface TranscriptionRes {
    transcriptionId: number
    content: string
    createdAt: string
  }

  export interface TranscriptionDetail {
    transcriptionId: number;
    content: string; // 내가 필사한 내용
    quoteContent: string; // 명언 원문
    quoteAuthor: string; // 명언 저자
    createdAt: string; // 생성 날짜
  }
  
  // 백엔드의 TranscriptionListRes DTO에 해당하는 타입
  export interface TranscriptionListRes {
    transcriptionList: TranscriptionDetail[];
    totalCount: number;
  }