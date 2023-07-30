import { rest } from 'msw';
import { BASE_URL } from '@apis/axios/client';
import goalRoom from '@mocks/fixtures/goalRoom';

const goalRoomHandler = [
  rest.get(`${BASE_URL}/goal-rooms/:goalRoomId`, (_, res, ctx) => {
    try {
      const goalRoomBrowseResponseData = goalRoom.getBrowsedGoalRoom();

      return res(ctx.status(200), ctx.json({ data: goalRoomBrowseResponseData }));
    } catch (err) {
      if (err instanceof Error) {
        return res(ctx.status(401), ctx.json({ message: err.message }));
      }
      return res(ctx.status(500));
    }
  }),
];

export default goalRoomHandler;
