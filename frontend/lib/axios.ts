import axios from 'axios';

const api = axios.create({
    baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
    withCredentials: true,
});

const getFromLS = (key: string) =>
    typeof window === "undefined" ? null : window.localStorage.getItem(key);

api.interceptors.request.use((config) => {
    const at = getFromLS("accessToken");
    if (at) {
        (config.headers ??= {} as any);
        (config.headers as any)["Authorization"] = `Bearer ${at}`;
    }
    return config;
});

export default api;