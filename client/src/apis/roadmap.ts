import {
  RoadmapDetailResponse,
  RoadmapValueRequest,
  RoadmapValueType,
} from '@myTypes/roadmap/remote';
import { CategoriesInfo } from '@constants/roadmap/category';
import client from './axios/client';

export const getRoadmapList = (
  categoryId?: keyof typeof CategoriesInfo,
  page = 1,
  size = 10,
  filterCond = 'LATEST'
) => {
  return client.get(`/roadmaps`, {
    params: {
      categoryId,
      page,
      size,
      filterCond,
    },
  });
};

export const getRoadmapById = (id: number) => {
  return client.get<RoadmapDetailResponse>(`/roadmaps/${id}`);
};

export const postCreateRoadmap = (roadmapValue: RoadmapValueType) => {
  const resposne = client.post<RoadmapValueRequest>('/roadmaps', roadmapValue);
  return resposne;
};
