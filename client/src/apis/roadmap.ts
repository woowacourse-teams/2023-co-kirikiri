import type { RoadmapDetailResponse } from '@myTypes/roadmap/remote';
import { RoadmapListResponse } from '@myTypes/roadmap/remote';
import client from './axios/client';
import { SelectedCategoryId } from '@/myTypes/roadmap/internal';

export const getRoadmapList = (
  categoryId?: SelectedCategoryId,
  page = 1,
  size = 10,
  filterCond = 'LATEST'
) => {
  return client.get<RoadmapListResponse>(`/roadmaps`, {
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
