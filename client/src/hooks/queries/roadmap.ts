import { SelectedCategoryId } from '@myTypes/roadmap/internal';
import { RoadmapValueType } from '@/myTypes/roadmap/remote';
import { getRoadmapDetail, getRoadmapList, postCreateRoadmap } from '@apis/roadmap';
import QUERY_KEYS from '@constants/@queryKeys/queryKeys';
import { useSuspendedQuery } from '@hooks/queries/useSuspendedQuery';
import { useMutation } from '@tanstack/react-query';

export const useRoadmapList = (
  categoryId?: SelectedCategoryId,
  page = 1,
  size = 10,
  filterCond = 'LATEST'
) => {
  const { data } = useSuspendedQuery(
    ['roadmapList', categoryId, page, size, filterCond],
    () => getRoadmapList(categoryId, page, size, filterCond)
  );

  return data;
};

export const useRoadmapDetail = (id: number) => {
  const { data } = useSuspendedQuery([QUERY_KEYS.roadmap.detail, id], () =>
    getRoadmapDetail(id)
  );

  return { roadmapInfo: data };
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
