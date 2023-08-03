import { RoadmapValueType } from '@/myTypes/roadmap/remote';
import { getRoadmapById, getRoadmapList, postCreateRoadmap } from '@apis/roadmap';
import QUERY_KEYS from '@constants/@queryKeys/queryKeys';
import { CategoriesInfo } from '@constants/roadmap/category';
import { useSuspendedQuery } from '@hooks/queries/useSuspendedQuery';
import { useMutation } from '@tanstack/react-query';

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

export const useRoadmapDetail = (id: number) => {
  return useSuspendedQuery(
    [QUERY_KEYS.roadmap.detail, id],
    () => (id ? getRoadmapById(id) : null),
    {
      enabled: !!id,
    }
  );
};

export const useCreateRoadmap = () => {
  const { mutate } = useMutation(
    (roadmapValue: RoadmapValueType) => postCreateRoadmap(roadmapValue),
    {
      onSuccess() {},
      onError() {},
    }
  );

  return {
    createRoadmap: mutate,
  };
};
