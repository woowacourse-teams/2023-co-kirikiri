import { rest } from 'msw';
import { BASE_URL } from '@apis/axios/client';
import { fixture } from '@mocks/fixtures/roadmapList';

export const roadmapsHandler = [
  rest.get(`${BASE_URL}/roadmaps`, (_, res, ctx) => {
    try {
      const readmapsBrowseResponseData = fixture.getBrowsedRoadmaps();

      return res(ctx.status(200), ctx.json({ data: readmapsBrowseResponseData }));
    } catch (err) {
      if (err instanceof Error) {
        return res(ctx.status(401), ctx.json({ message: err.message }));
      }

      return res(ctx.status(500));
    }
  }),
];
