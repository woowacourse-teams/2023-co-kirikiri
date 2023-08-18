import { RoadmapListRequest } from '@/myTypes/roadmap/remote';
import {
  getRoadmapDetail,
  getRoadmapList,
  getSearchRoadmapList,
  getMyRoadmapList,
  postCreateRoadmap,
} from '@apis/roadmap';
import QUERY_KEYS from '@constants/@queryKeys/queryKeys';
import { useSuspendedQuery } from '@hooks/queries/useSuspendedQuery';
import { useInfiniteQuery, useMutation, useQueryClient } from '@tanstack/react-query';

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

export const useSearchRoadmapList = ({
  category,
  search,
  filterCond = 'LATEST',
  lastId = '',
  size = 10,
}: any) => {
  const { data, fetchNextPage } = useInfiniteQuery(
    ['searchRoadmapList', category, search, filterCond, lastId, size],
    ({ pageParams }: any) =>
      getSearchRoadmapList({
        category,
        search,
        filterCond,
        lastId: pageParams,
        size,
      }),
    {
      getNextPageParam: (lastPage) =>
        lastPage.hasNext
          ? lastPage.responses[lastPage.responses.length - 1]?.roadmapId
          : undefined,
    }
  );
  const responses = data?.pages.flatMap((page) => page.responses) || [];

  const hasNext = Boolean(data?.pages[data.pages.length - 1]?.hasNext);

  return { searchRoadmapListResponse: { responses, hasNext }, fetchNextPage };
};

export const useRoadmapDetail = (id: number) => {
  const { data } = useSuspendedQuery([QUERY_KEYS.roadmap.detail, id], () =>
    getRoadmapDetail(id)
  );

  return { roadmapInfo: data };
};

export const useCreateRoadmap = () => {
  const queryClient = useQueryClient();

  const { mutate } = useMutation((formData: FormData) => postCreateRoadmap(formData), {
    onSuccess() {
      queryClient.invalidateQueries(['searchRoadmapList']);
    },
    onError() {},
  });

  return {
    createRoadmap: mutate,
  };
};

export const useMyRoadmapList = () => {
  const size = 10;
  const lastId = undefined;

  const { data } = useSuspendedQuery([QUERY_KEYS.roadmap.myRoadmap, size, lastId], () =>
    getMyRoadmapList(10, lastId)
  );

  return { myRoadmapList: data };
};
