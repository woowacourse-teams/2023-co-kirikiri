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

let globalRetryCount = 0;

client.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    if (globalRetryCount >= 3) {
      window.dispatchEvent(new Event('unauthorized'));
      return Promise.reject(error);
    }

    if (
      error.response &&
      error.response.status === 401 &&
      error.response.data.message === 'Expired Token'
    ) {
      globalRetryCount++;

      const refreshToken = getCookie('refresh_token');

      try {
        const { data } = await client.post('/auth/reissue', { refreshToken });

        setCookie('access_token', data.accessToken);
        setCookie('refresh_token', data.refreshToken);

        return client(originalRequest);
      } catch (reissueError) {
        const axiosError = reissueError as AxiosError<ErrorData>;

        if (axiosError.response?.status === 401) {
          window.dispatchEvent(new Event('unauthorized'));
          return Promise.reject(reissueError);
        } else {
          return Promise.reject(reissueError);
        }
      }
    } else if (
      error.response &&
      error.response.status === 501 &&
      error.response.data.message.includes(
        'Row was updated or deleted by another transaction'
      )
    ) {
      return null;
    } else {
      return Promise.reject(error);
    }
  }
);

export default client;
