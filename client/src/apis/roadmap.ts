import type { RoadmapDetailResponse } from '@myTypes/roadmap/remote';
import { RoadmapListResponse } from '@myTypes/roadmap/remote';
import client from './axios/client';
import { SelectedCategoryId } from '@/myTypes/roadmap/internal';

export const getRoadmapList = async (
  categoryId?: SelectedCategoryId,
  page = 1,
  size = 10,
  filterCond = 'LATEST'
) => {
  const { data } = await client.get<RoadmapListResponse>(`/roadmaps`, {
    params: {
      ...(categoryId && { categoryId }),
      page,
      size,
      filterCond,
    },
  });

  return data;
};

export const getRoadmapById = async (id: number) => {
  const { data } = await client.get<RoadmapDetailResponse>(`/roadmaps/${id}`);

  return data;
};
