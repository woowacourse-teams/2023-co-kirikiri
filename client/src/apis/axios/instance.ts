import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';

const instance = axios.create({
  baseURL: ``,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const get = <T = any>(url: string, config?: AxiosRequestConfig) => {
  return instance.get<T>(url, config);
};

export const post = <T = any, R = AxiosResponse<T>>(
  url: string,
  data?: any,
  config?: AxiosRequestConfig
): Promise<R> => {
  return instance.post(url, data, config);
};
