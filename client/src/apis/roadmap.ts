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

export const getRoadmapDetail = async (id: number): Promise<RoadmapDetailResponse> => {
  const { data } = await client.get<RoadmapDetailResponse>(`/roadmaps/${id}`);

  return data;
};

export const postCreateRoadmap = (roadmapValue: RoadmapValueType) => {
  const resposne = client.post<RoadmapValueRequest>('/roadmaps', roadmapValue);
  return resposne;
};
