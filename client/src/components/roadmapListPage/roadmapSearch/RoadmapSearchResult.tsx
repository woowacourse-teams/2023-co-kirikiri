import AsyncBoundary from '@/components/_common/errorBoundary/AsyncBoundary';
import RoadmapItem from '@/components/_common/roadmapItem/RoadmapItem';
import WavyLoading from '@/components/_common/wavyLoading/WavyLoading';
import { useSearchRoadmapList } from '@/hooks/queries/roadmap';
import { useInfiniteScroll } from '@/hooks/_common/useInfiniteScroll';
import { useLocation, useNavigate } from 'react-router-dom';
import NoResult from './NoResult';
import * as S from './roadmapSearch.styles';

const RoadmapSearchResult = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const searchParams = new URLSearchParams(location.search);
  const category = searchParams.get('searchCategory');
  const search = searchParams.get('searchWord');

  const { searchRoadmapListResponse, fetchNextPage } = useSearchRoadmapList({
    category,
    search,
  });
  const { hasNext, responses: roadmapList } = searchRoadmapListResponse;
  const loadMoreRef = useInfiniteScroll({
    hasNextPage: hasNext,
    fetchNextPage,
  });

  const moveRoadmapCreatePage = () => {
    navigate('/roadmap-create');
  };

  return (
    <>
      <AsyncBoundary>
        {roadmapList.length === 0 && <NoResult />}
        <S.RoadmapList aria-label='로드맵 목록'>
          {roadmapList.map((item) => (
            <RoadmapItem key={item.roadmapId} item={item} roadmapId={item.roadmapId} />
          ))}
          {hasNext && <WavyLoading loadMoreRef={loadMoreRef} />}
          <S.CreateRoadmapButton onClick={moveRoadmapCreatePage}>+</S.CreateRoadmapButton>
        </S.RoadmapList>
      </AsyncBoundary>
    </>
  );
};

export default RoadmapSearchResult;
