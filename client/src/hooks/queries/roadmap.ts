import { RoadmapListRequest } from '@/myTypes/roadmap/remote';
import { getRoadmapDetail, getRoadmapList, postCreateRoadmap } from '@apis/roadmap';
import QUERY_KEYS from '@constants/@queryKeys/queryKeys';
import { useSuspendedQuery } from '@hooks/queries/useSuspendedQuery';
import { useInfiniteQuery, useMutation } from '@tanstack/react-query';

export const useRoadmapList = ({
  categoryId,
  size = 6,
  filterCond = 'LATEST',
  lastId,
}: RoadmapListRequest) => {
  const { data, fetchNextPage } = useInfiniteQuery(
    [QUERY_KEYS.roadmap.list, categoryId, size, filterCond, lastId],
    ({ pageParam }) =>
      getRoadmapList({ categoryId, size, filterCond, lastId: pageParam }),
    {
      getNextPageParam: (lastPage) =>
        lastPage.hasNext
          ? lastPage.responses[lastPage.responses.length - 1]?.roadmapId
          : undefined,
    }
  );

  const responses = data?.pages.flatMap((page) => page.responses) || [];

  const hasNext = Boolean(data?.pages[data.pages.length - 1]?.hasNext);

  return { roadmapListResponse: { responses, hasNext }, fetchNextPage };
};

export const useRoadmapDetail = (id: number) => {
  const { data } = useSuspendedQuery([QUERY_KEYS.roadmap.detail, id], () =>
    getRoadmapDetail(id)
  );

  return { roadmapInfo: data };
};

export const useCreateRoadmap = () => {
  const { mutate } = useMutation((formData: FormData) => postCreateRoadmap(formData), {
    onSuccess() {},
    onError() {},
  });

  return {
    createRoadmap: mutate,
  };
};
