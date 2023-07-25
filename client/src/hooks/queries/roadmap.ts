import { useQuery } from '@tanstack/react-query';
import { getRoadmapById, getRoadmapList } from '@apis/roadmap';
import QUERY_KEYS from '@constants/@queryKeys/queryKeys';
import { CategoriesInfo } from '@constants/roadmap/category';

export const useRoadmapList = (
  categoryId?: keyof typeof CategoriesInfo,
  page = 1,
  size = 10,
  filterCond = 'LATEST'
) => {
  return useQuery(['roadmapList', categoryId, page, size, filterCond], () =>
    getRoadmapList(categoryId, page, size, filterCond)
  );
};

export const useRoadmapDetail = (id: string) => {
  return useQuery(
    [QUERY_KEYS.roadmap.detail, id],
    () => (id ? getRoadmapById(id) : null),
    {
      enabled: !!id,
    }
  );
};
