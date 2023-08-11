import {
  RoadmapListRequest,
  RoadmapListResponse,
  RoadmapValueType,
} from '@/myTypes/roadmap/remote';
import { getRoadmapDetail, getRoadmapList, postCreateRoadmap } from '@apis/roadmap';
import QUERY_KEYS from '@constants/@queryKeys/queryKeys';
import { useSuspendedQuery } from '@hooks/queries/useSuspendedQuery';
import { useInfiniteQuery, useMutation } from '@tanstack/react-query';
import { RoadmapItemType } from '@myTypes/roadmap/internal';

export const useRoadmapList = ({
  categoryId,
  size = 4,
  filterCond = 'LATEST',
  lastId,
}: RoadmapListRequest) => {
  const { data, fetchNextPage } = useInfiniteQuery<RoadmapListResponse>(
    ['roadmapList', categoryId, size, filterCond, lastId],
    () => getRoadmapList({ categoryId, size, filterCond, lastId }),
    {
      getNextPageParam: (lastPage) => {
        const { roadmapId: lastRoadmapId } =
          lastPage.responses[lastPage.responses.length - 1];
        return lastPage.hasNext ? lastRoadmapId : undefined;
      },
    }
  );

  const responses =
    data?.pages.reduce((allResponses, page) => {
      return [...allResponses, ...page.responses];
    }, [] as RoadmapItemType[]) || [];

  const hasNextPage = Boolean(data?.pages[data.pages.length - 1]?.hasNext);

  return {
    roadmapListResponse: { responses, hasNext: hasNextPage },
    fetchNextPage,
  };
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
