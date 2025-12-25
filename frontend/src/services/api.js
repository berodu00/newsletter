import axios from 'axios';

const api = axios.create({
    baseURL: '/api', // Proxy handles forwarding to backend
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor for JWT
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Response interceptor for handling 401/Refresh Token
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                const refreshToken = localStorage.getItem('refreshToken');
                if (refreshToken) {
                    const response = await axios.post('/api/auth/refresh', { refreshToken });
                    const { accessToken } = response.data;
                    localStorage.setItem('accessToken', accessToken);
                    originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;
                    return api(originalRequest);
                }
            } catch (err) {
                // Refresh failed - logout
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                localStorage.removeItem('user');
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

export default api;
