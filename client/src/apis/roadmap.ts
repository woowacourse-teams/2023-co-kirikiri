import type {
  RoadmapDetailResponse,
  RoadmapListRequest,
  RoadmapListResponse,
  RoadmapValueRequest,
  RoadmapValueType,
} from '@myTypes/roadmap/remote';
import client from './axios/client';

export const getRoadmapList = async ({
  categoryId,
  size,
  filterCond,
  lastId,
}: RoadmapListRequest) => {
  const { data } = await client.get<RoadmapListResponse>(`/roadmaps`, {
    params: {
      ...(categoryId && { categoryId }),
      ...(lastId && { lastId }),
      size,
      filterCond,
    },
  });

  return data;
};

// 부엉이와 코드 충돌 방지하기 위해 request & response 타입은 any를 써두겠습니다 :)
export const getSearchRoadmapList = async ({
  roadmapTitle,
  creatorId,
  tagName,
  filterCond = 'LATEST',
  lastId,
  size = 10,
}: any) => {
  const { data } = await client.get<any>(
    `/api/roadmaps/search?roadmapTitle=${roadmapTitle}&lastId=${lastId}&creatorId=${creatorId}&tagName=${tagName}&filterCond=${filterCond}&size=${size}`
  );

  return data;
};

export const getRoadmapDetail = async (id: number): Promise<RoadmapDetailResponse> => {
  const { data } = await client.get<RoadmapDetailResponse>(`/roadmaps/${id}`);

  return data;
};

export const postCreateRoadmap = (roadmapValue: RoadmapValueType) => {
  const resposne = client.post<RoadmapValueRequest>('/roadmaps', roadmapValue);
  return resposne;
};
