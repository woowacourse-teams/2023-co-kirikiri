import type {
  RoadmapDetailResponse,
  RoadmapListRequest,
  RoadmapListResponse,
  RoadmapValueRequest,
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

export const getSearchRoadmapList = async ({
  category,
  search,
  filterCond = 'LATEST',
  lastId = '',
  size = 10,
}: any) => {
  const { data } = await client.get<RoadmapListResponse>(`/roadmaps/search`, {
    params: {
      [category]: search,
      filterCond,
      ...(lastId && { lastId }),
      size,
    },
  });

  return data;
};

export const getRoadmapDetail = async (id: number): Promise<RoadmapDetailResponse> => {
  const { data } = await client.get<RoadmapDetailResponse>(`/roadmaps/${id}`);

  return data;
};

export const postCreateRoadmap = (roadmapValue: FormData) => {
  const resposne = client.post<RoadmapValueRequest>('/roadmaps', roadmapValue, {
    headers: {
      'Content-Type': 'multipart/form-data;charset=UTF-8',
    },
  });
  return resposne;
};

export const getMyRoadmapList = async (size: number, lastId?: number) => {
  const { data } = await client.get<RoadmapListResponse>('/roadmaps/me', {
    params: {
      size,
      ...(lastId && { lastId }),
    },
  });

  return data;
};
