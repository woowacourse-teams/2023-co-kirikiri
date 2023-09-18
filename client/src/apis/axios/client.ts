import axios, { AxiosError } from 'axios';
import { getCookie, setCookie } from '@utils/_common/cookies';

type ErrorData = {
  message: string;
};

export const BASE_URL = `${
  process.env.NODE_ENV === 'production' ? process.env.PROD_SERVER : process.env.DEV_SERVER
}`;

const client = axios.create({
  baseURL: BASE_URL,
});

client.interceptors.request.use((config) => {
  const token = getCookie('access_token');

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

client.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (!originalRequest.retryCount) {
      originalRequest.retryCount = 0;
    }

    if (error.response) {
      if (
        error.response.status === 401 &&
        error.response.data.message === 'Expired Token'
      ) {
        if (originalRequest.retryCount >= 3) {
          window.location.href = '/login';
          return;
        }
        originalRequest.retryCount++;

        const refreshToken = getCookie('refresh_token');

        try {
          const { data } = await client.post('/auth/reissue', { refreshToken });

          setCookie('access_token', data.accessToken);
          setCookie('refresh_token', data.refreshToken);

          console.log(originalRequest, 'ORRRRRRRR');

          return client(originalRequest);
        } catch (reissueError) {
          const axiosError = reissueError as AxiosError<ErrorData>;

          if (
            axiosError.response?.status === 401 &&
            axiosError.response?.data?.message === '토큰이 유효하지 않습니다.'
          ) {
            return client(originalRequest);
          } else {
            throw reissueError;
          }
        }
      } else {
        throw error;
      }
    }
    throw error;
  }
);

export default client;
