import type { RoadmapDetailResponse } from '@myTypes/roadmap/remote';
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

export const getRoadmapById = (id: string) => {
  return client.get<RoadmapDetailResponse>(`/roadmaps/${id}`);
};
