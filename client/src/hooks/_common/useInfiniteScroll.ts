import { useEffect, useRef } from 'react';
import useIntersection from '@hooks/_common/useIntersection';

type UseInfiniteScrollProps = {
  hasNextPage: boolean;
  fetchNextPage: () => void;
};

export const useInfiniteScroll = ({
  hasNextPage,
  fetchNextPage,
}: UseInfiniteScrollProps) => {
  const loadMoreRef = useRef(null);

  const intersection = useIntersection(loadMoreRef, {
    root: null,
    rootMargin: '0px',
    threshold: 0.2,
  });

  useEffect(() => {
    if (intersection?.isIntersecting && hasNextPage) {
      fetchNextPage();
    }
  }, [intersection, hasNextPage, fetchNextPage]);

  return loadMoreRef;
};
