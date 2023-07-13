import axios from 'axios';

export const BASE_URL = `${
  process.env.NODE_ENV === 'production'
    ? process.env.PROD_SERVER
    : process.env.API_TEST_SERVER
}`;

export const client = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});
