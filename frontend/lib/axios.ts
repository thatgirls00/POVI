import axios from 'axios';

const api = axios.create({
    baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
    withCredentials: true,
});

// SSR 안전 가드
const getFromLS = (key: string) =>
    typeof window === "undefined" ? null : window.localStorage.getItem(key);

// 요청 인터셉터: Authorization 주입
api.interceptors.request.use((config) => {
    const at = getFromLS("accessToken"); // 로그인 시 여기에 저장되어 있어야 함
    if (at) {
        (config.headers ??= {});
        (config.headers as any).Authorization = `Bearer ${at}`;
    }
    return config;
});

export default api;