"use client"

import { useState, useEffect } from "react"
import { Header } from "@/components/header"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Sparkles, Check, Coffee, Heart, Sun, History, Loader2, Cloud, CloudRain, CloudSnow, Wind, Thermometer } from "lucide-react"
import api from "@/lib/axios"

// 백엔드 EmotionType과 매핑
const emotions = [
  { emoji: "😊", label: "행복해요", color: "hover:bg-yellow-100", emotionType: "HAPPY" },
  { emoji: "😂", label: "즐거워요", color: "hover:bg-orange-100", emotionType: "JOYFUL" },
  { emoji: "😌", label: "평온해요", color: "hover:bg-green-100", emotionType: "CALM" },
  { emoji: "😐", label: "그저그래요", color: "hover:bg-gray-100", emotionType: "NEUTRAL" },
  { emoji: "😔", label: "우울해요", color: "hover:bg-blue-100", emotionType: "DEPRESSED" },
  { emoji: "😢", label: "슬퍼요", color: "hover:bg-indigo-100", emotionType: "SAD" },
  { emoji: "😭", label: "힘들어요", color: "hover:bg-purple-100", emotionType: "TIRED" },
  { emoji: "😤", label: "화나요", color: "hover:bg-red-100", emotionType: "ANGRY" },
]

// 타입 정의
interface MissionResponse {
  missionId: number
  title: string
  description: string
  status: "IN_PROGRESS" | "COMPLETED"
  userMissionId: number
}

interface MissionHistoryResponse {
  missionDate: string
  missions: MissionResponse[]
  completedCount: number
  totalCount: number
  completionRate: number
}

// 날씨 정보 타입
interface WeatherInfo {
  main: string
  description: string
  temp: number
  windSpeed: number
}

// 아이콘 매핑
const getMissionIcon = (title: string) => {
  if (title.includes("차") || title.includes("음료")) return Coffee
  if (title.includes("감사") || title.includes("감정")) return Heart
  if (title.includes("산책") || title.includes("운동") || title.includes("밖")) return Sun
  return Coffee // 기본값
}

// 날씨 아이콘 매핑
const getWeatherIcon = (weatherMain: string) => {
  switch (weatherMain.toLowerCase()) {
    case 'clear': return Sun
    case 'clouds': return Cloud
    case 'rain': return CloudRain
    case 'snow': return CloudSnow
    case 'wind': return Wind
    default: return Thermometer
  }
}

// 날씨 한글 변환
const getWeatherKorean = (weatherMain: string) => {
  switch (weatherMain.toLowerCase()) {
    case 'clear': return '맑음'
    case 'clouds': return '구름'
    case 'rain': return '비'
    case 'snow': return '눈'
    case 'wind': return '바람'
    case 'thunderstorm': return '뇌우'
    case 'drizzle': return '이슬비'
    case 'mist': case 'fog': case 'haze': return '안개'
    default: return weatherMain
  }
}

export default function MissionsPage() {
  const [missionList, setMissionList] = useState<MissionResponse[]>([])
  const [missionHistory, setMissionHistory] = useState<MissionHistoryResponse[]>([])
  const [showEmotionDialog, setShowEmotionDialog] = useState(false)
  const [loading, setLoading] = useState(false)
  const [userLocation, setUserLocation] = useState<{ latitude: number; longitude: number } | null>(null)
  const [weatherInfo, setWeatherInfo] = useState<WeatherInfo | null>(null)
  const [error, setError] = useState<string | null>(null)

  const completedCount = missionList.filter((m) => m.status === "COMPLETED").length

  // 사용자 위치 가져오기
  useEffect(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setUserLocation({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
          })
        },
        (error) => {
          // 기본값으로 서울 좌표 설정
          setUserLocation({ latitude: 37.5665, longitude: 126.9780 })
        }
      )
    } else {
      // 기본값으로 서울 좌표 설정
      setUserLocation({ latitude: 37.5665, longitude: 126.9780 })
    }
  }, [])

  // 날씨 정보 가져오기 (백엔드 API 호출)
  const fetchWeatherInfo = async (lat: number, lon: number) => {
    try {
      const response = await api.get("/api/weather", {
        params: {
          latitude: lat,
          longitude: lon
        }
      })
      
      setWeatherInfo({
        main: response.data.main,
        description: response.data.description,
        temp: response.data.temp,
        windSpeed: response.data.windSpeed
      })
    } catch (error) {
      // 날씨 정보 가져오기 실패 시 기본값 설정
      setWeatherInfo({
        main: "Clear",
        description: "clear weather",
        temp: 20,
        windSpeed: 0
      })
    }
  }

  // 오늘의 미션 조회
  const fetchTodayMissions = async () => {
    try {
      setLoading(true)
      const response = await api.get("/api/missions/today")
      
      if (response.status === 204) {
        setMissionList([])
        setShowEmotionDialog(true) // 미션이 없으면 감정 선택 다이얼로그 열기
      } else {
        setMissionList(response.data)
        setShowEmotionDialog(false) // 미션이 있으면 다이얼로그 닫기
      }
    } catch (error: any) {
      if (error.response?.status === 401) {
        window.location.href = "/login"
      }
    } finally {
      setLoading(false)
    }
  }

  // 미션 이력 조회
  const fetchMissionHistory = async () => {
    try {
      const response = await api.get("/api/missions/history")
      if (response.status === 204) {
        setMissionHistory([])
      } else {
        setMissionHistory(response.data)
      }
    } catch (error) {
      // 미션 이력 조회 실패 시 무시
    }
  }

  // 오늘의 미션 생성
  const createTodayMissions = async (emotionType: string) => {
    if (!userLocation) {
      return
    }
    
    try {
      setLoading(true)
      
      const response = await api.post("/api/missions/today", {
        emotionType,
        latitude: userLocation.latitude,
        longitude: userLocation.longitude,
      })
      setMissionList(response.data)
      setShowEmotionDialog(false) // 미션 생성 성공 시 다이얼로그 닫기
    } catch (error: any) {
      // 사용자에게 에러 메시지 표시
      const errorMessage = error.response?.data?.message || error.message || "알 수 없는 오류가 발생했습니다."
      setError(errorMessage)
      alert(`미션 생성에 실패했습니다: ${errorMessage}`)
    } finally {
      setLoading(false)
    }
  }

  // 미션 상태 업데이트
  const toggleMission = async (userMissionId: number, currentStatus: string) => {
    try {
      const newStatus = currentStatus === "COMPLETED" ? "IN_PROGRESS" : "COMPLETED"
      await api.patch(`/api/missions/${userMissionId}/status`, {
        status: newStatus,
      })
      
      // 로컬 상태 업데이트
      setMissionList(missionList.map((m) => 
        m.userMissionId === userMissionId ? { ...m, status: newStatus } : m
      ))
    } catch (error) {
      // 미션 상태 업데이트 실패 시 무시
    }
  }

  const selectEmotion = async (emotionType: string) => {
    setError(null)
    await createTodayMissions(emotionType)
  }

  // 컴포넌트 마운트 시 오늘의 미션 조회
  useEffect(() => {
    const initializeMissions = async () => {
      await fetchTodayMissions()
      await fetchMissionHistory()
    }
    initializeMissions()
  }, [])

  // 위치 정보가 있으면 날씨 정보 가져오기
  useEffect(() => {
    if (userLocation) {
      fetchWeatherInfo(userLocation.latitude, userLocation.longitude)
    }
  }, [userLocation])

  return (
    <div className="min-h-screen">
      <Header />

      <Dialog open={showEmotionDialog} onOpenChange={setShowEmotionDialog}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle className="text-2xl text-center">지금 기분이 어떠신가요?</DialogTitle>
            <p className="text-center text-muted-foreground">감정에 맞는 맞춤 미션을 추천해드릴게요</p>
          </DialogHeader>
          <div className="grid grid-cols-4 gap-4 py-6">
            {emotions.map((emotion) => (
              <button
                key={emotion.label}
                onClick={() => selectEmotion(emotion.emotionType)}
                disabled={loading}
                className={`flex flex-col items-center gap-2 p-4 rounded-xl transition-all ${emotion.color} hover:scale-105 active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed`}
              >
                <span className="text-4xl">{emotion.emoji}</span>
                <span className="text-xs text-center">{emotion.label}</span>
              </button>
            ))}
          </div>
          {loading && (
            <div className="flex items-center justify-center py-4">
              <Loader2 className="h-6 w-6 animate-spin mr-2" />
              <span className="text-sm text-muted-foreground">미션을 생성하는 중...</span>
            </div>
          )}
        </DialogContent>
      </Dialog>

      <main className="container py-8 md:py-12 max-w-4xl">
        <div className="mb-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold mb-2">오늘의 미션</h1>
              <p className="text-muted-foreground">감정 분석 기반 맞춤 미션으로 하루를 더 풍요롭게</p>
            </div>
            {missionList.length === 0 && (
              <Button 
                onClick={() => {
                  setError(null)
                  setShowEmotionDialog(true)
                }}
                disabled={loading}
              >
                새 미션 생성
              </Button>
            )}
          </div>
          
          {/* 날씨 정보 표시 */}
          {weatherInfo && (
            <Card className="mt-4 p-4 bg-gradient-to-r from-blue-50 to-indigo-50 border-blue-200">
              <div className="flex items-center gap-3">
                <div className="flex-shrink-0 w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center">
                  {(() => {
                    const WeatherIcon = getWeatherIcon(weatherInfo.main)
                    return <WeatherIcon className="h-5 w-5 text-blue-600" />
                  })()}
                </div>
                <div className="flex-1">
                  <div className="flex items-center gap-2">
                    <span className="font-semibold text-lg">{getWeatherKorean(weatherInfo.main)}</span>
                    <span className="text-sm text-muted-foreground">
                      {weatherInfo.temp}°C
                    </span>
                  </div>
                  <p className="text-sm text-muted-foreground">
                    바람 {weatherInfo.windSpeed}m/s
                  </p>
                </div>
              </div>
            </Card>
          )}
          
          {/* 에러 메시지 표시 */}
          {error && (
            <Card className="mt-4 p-4 bg-red-50 border-red-200">
              <div className="flex items-center gap-2">
                <div className="text-red-600">⚠️</div>
                <div>
                  <h3 className="font-semibold text-red-800">오류가 발생했습니다</h3>
                  <p className="text-sm text-red-600">{error}</p>
                </div>
                <Button 
                  variant="outline" 
                  size="sm" 
                  onClick={() => setError(null)}
                  className="ml-auto"
                >
                  닫기
                </Button>
              </div>
            </Card>
          )}
        </div>

        {/* AI Recommendation */}
        <Tabs defaultValue="today" className="space-y-6">
          <TabsList className="grid w-full max-w-md grid-cols-2">
            <TabsTrigger value="today">오늘의 미션</TabsTrigger>
            <TabsTrigger value="history">
              <History className="h-4 w-4 mr-2" />
              미션 이력
            </TabsTrigger>
          </TabsList>

          <TabsContent value="today" className="space-y-6">
            {missionList.length > 0 && (
              <Card className="p-6 bg-gradient-to-br from-accent/30 to-secondary/40 border-none">
                <div className="flex items-start gap-4">
                  <div className="flex-shrink-0 w-12 h-12 rounded-full bg-primary/20 flex items-center justify-center">
                    <Sparkles className="h-6 w-6 text-primary" />
                  </div>
                  <div>
                    <h2 className="font-semibold text-lg mb-2">AI 추천</h2>
                    <p className="text-muted-foreground leading-relaxed">
                      오늘의 미션이 생성되었습니다! 
                      {weatherInfo && ` 오늘 날씨는 ${getWeatherKorean(weatherInfo.main)}이고 ${weatherInfo.temp}°C입니다.`}
                      즐거운 하루 되세요.
                    </p>
                  </div>
                </div>
              </Card>
            )}

            <Card className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="font-semibold text-lg mb-1">오늘의 진행도</h3>
                  <p className="text-sm text-muted-foreground">
                    {completedCount} / {missionList.length} 완료
                  </p>
                </div>
              </div>
            </Card>

            {/* Missions */}
            <div className="space-y-4">
              <h2 className="text-xl font-semibold">추천 미션</h2>
              {loading ? (
                <div className="flex items-center justify-center py-8">
                  <Loader2 className="h-8 w-8 animate-spin" />
                  <span className="ml-2">미션을 불러오는 중...</span>
                </div>
              ) : missionList.length === 0 ? (
                <Card className="p-6 text-center">
                  <p className="text-muted-foreground">아직 미션이 없습니다. 감정을 선택해주세요.</p>
                </Card>
              ) : (
                missionList.map((mission) => {
                  const Icon = getMissionIcon(mission.title)
                  const isCompleted = mission.status === "COMPLETED"
                  return (
                    <Card
                      key={mission.missionId}
                      className={`p-6 transition-all ${isCompleted ? "bg-muted/50 border-primary/30" : "hover:shadow-lg"}`}
                    >
                      <div className="flex items-start gap-4">
                        <div
                          className={`flex-shrink-0 w-12 h-12 rounded-full flex items-center justify-center ${
                            isCompleted ? "bg-primary/20" : "bg-secondary"
                          }`}
                        >
                          {isCompleted ? (
                            <Check className="h-6 w-6 text-primary" />
                          ) : (
                            <Icon className="h-6 w-6 text-foreground" />
                          )}
                        </div>
                        <div className="flex-1">
                          <div className="flex items-start justify-between mb-2">
                            <div>
                              <h3
                                className={`font-semibold text-lg mb-1 ${isCompleted ? "line-through text-muted-foreground" : ""}`}
                              >
                                {mission.title}
                              </h3>
                              <p className="text-sm text-muted-foreground mb-2">{mission.description}</p>
                            </div>
                          </div>
                        </div>
                        <Button
                          variant={isCompleted ? "outline" : "default"}
                          size="sm"
                          onClick={() => toggleMission(mission.userMissionId, mission.status)}
                          disabled={loading}
                        >
                          {isCompleted ? "완료 취소" : "완료"}
                        </Button>
                      </div>
                    </Card>
                  )
                })
              )}
            </div>

            {/* Motivation */}
            {missionList.length > 0 && completedCount === missionList.length && (
              <Card className="p-6 bg-primary/10 border-primary/20">
                <div className="text-center">
                  <div className="text-5xl mb-4">🎉</div>
                  <h3 className="font-semibold text-xl mb-2">오늘의 미션을 모두 완료했어요!</h3>
                  <p className="text-muted-foreground">정말 잘하셨어요. 내일도 함께 해요!</p>
                </div>
              </Card>
            )}
          </TabsContent>

          {/* Mission History */}
          <TabsContent value="history" className="space-y-4">
            <h2 className="text-xl font-semibold">지난 미션 이력</h2>
            {missionHistory.length === 0 ? (
              <Card className="p-6 text-center">
                <p className="text-muted-foreground">아직 미션 이력이 없습니다.</p>
              </Card>
            ) : (
              missionHistory.map((day, idx) => (
                <Card key={idx} className="p-6">
                  <h3 className="font-semibold mb-4">
                    {new Date(day.missionDate).toLocaleDateString('ko-KR', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric',
                      weekday: 'long'
                    })}
                  </h3>
                  <div className="space-y-3">
                    {day.missions.map((mission, mIdx) => (
                      <div key={mIdx} className="flex items-center gap-3">
                        <div
                          className={`flex-shrink-0 w-6 h-6 rounded-full flex items-center justify-center ${
                            mission.status === "COMPLETED" ? "bg-primary/20" : "bg-muted"
                          }`}
                        >
                          {mission.status === "COMPLETED" && <Check className="h-4 w-4 text-primary" />}
                        </div>
                        <span className={mission.status === "COMPLETED" ? "text-muted-foreground" : "text-foreground"}>
                          {mission.title}
                        </span>
                        <span className="text-xs text-muted-foreground ml-auto">
                          {mission.status === "COMPLETED" ? "완료" : "미완료"}
                        </span>
                      </div>
                    ))}
                  </div>
                  <div className="mt-4 pt-4 border-t">
                    <p className="text-sm text-muted-foreground">
                      완료율: {day.completionRate}% ({day.completedCount}/{day.totalCount})
                    </p>
                  </div>
                </Card>
              ))
            )}
          </TabsContent>
        </Tabs>
      </main>
    </div>
  )
}
