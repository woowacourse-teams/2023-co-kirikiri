import { SelectedCategoryId } from '@myTypes/roadmap/internal';
import { getRoadmapById, getRoadmapList } from '@apis/roadmap';
import QUERY_KEYS from '@constants/@queryKeys/queryKeys';
import { useSuspendedQuery } from '@hooks/queries/useSuspendedQuery';

export const useRoadmapList = (
  categoryId?: SelectedCategoryId,
  page = 1,
  size = 10,
  filterCond = 'LATEST'
) => {
  return useSuspendedQuery(['roadmapList', categoryId, page, size, filterCond], () =>
    getRoadmapList(categoryId, page, size, filterCond)
  );
};

export const useRoadmapDetail = (id: number) => {
  return useSuspendedQuery(
    [QUERY_KEYS.roadmap.detail, id],
    () => (id ? getRoadmapById(id) : null),
    {
      enabled: !!id,
    }
  );
};
