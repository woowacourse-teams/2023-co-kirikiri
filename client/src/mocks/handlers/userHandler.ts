import { BASE_URL } from '@apis/axios/client';
import { rest } from 'msw';
import { UserLoginRequest, UserLoginResponse } from '@myTypes/user/remote';

export const userHandler = [
  rest.post<UserLoginRequest, UserLoginResponse>(
    `${BASE_URL}/auth/login`,
    (req, res, ctx) => {
      const { identifier, password } = req.body;

      if (identifier === 'msw' && password === 'password') {
        const response: UserLoginResponse = {
          accessToken: 'accessTT',
          refreshToken: 'refreshTT',
        };
        return res(ctx.status(200), ctx.json(response));
      }

      return res(ctx.status(401), ctx.json({ message: 'Invalid username or password' }));
    }
  ),
];
