import { getRoadmapById, getRoadmapList } from '@apis/roadmap';
import QUERY_KEYS from '@constants/@queryKeys/queryKeys';
import { CategoriesInfo } from '@constants/roadmap/category';
import { useSuspendedQuery } from '@hooks/queries/useSuspendedQuery';

export const useRoadmapList = (
  categoryId?: keyof typeof CategoriesInfo,
  page = 1,
  size = 10,
  filterCond = 'LATEST'
) => {
  return useSuspendedQuery(['roadmapList', categoryId, page, size, filterCond], () =>
    getRoadmapList(categoryId, page, size, filterCond)
  );
};

export const useRoadmapDetail = (id: string) => {
  return useSuspendedQuery(
    [QUERY_KEYS.roadmap.detail, id],
    () => (id ? getRoadmapById(id) : null),
    {
      enabled: !!id,
    }
  );
};
