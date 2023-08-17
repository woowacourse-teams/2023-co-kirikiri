import RoadmapItem from '@/components/_common/roadmapItem/RoadmapItem';
import WavyLoading from '@/components/_common/wavyLoading/WavyLoading';
import { useSearchRoadmapList } from '@/hooks/queries/roadmap';
import { useInfiniteScroll } from '@/hooks/_common/useInfiniteScroll';
import useValidParams from '@/hooks/_common/useValidParams';
import { useNavigate } from 'react-router-dom';
import * as S from './roadmapSearch.styles';

const RoadmapSearchResult = () => {
  const { category, search } = useValidParams();

  const { searchRoadmapListResponse, fetchNextPage } = useSearchRoadmapList({
    category,
    search,
  });
  const loadMoreRef = useInfiniteScroll({
    hasNextPage: searchRoadmapListResponse?.hasNext,
    fetchNextPage,
  });

  const navigate = useNavigate();

  const moveRoadmapCreatePage = () => {
    navigate('/roadmap-create');
  };

  return (
    <S.RoadmapList aria-label='로드맵 목록'>
      {searchRoadmapListResponse.responses.map((item) => (
        <RoadmapItem key={item.roadmapId} item={item} roadmapId={item.roadmapId} />
      ))}
      {searchRoadmapListResponse?.hasNext && <WavyLoading loadMoreRef={loadMoreRef} />}
      <S.CreateRoadmapButton onClick={moveRoadmapCreatePage}>
        로드맵 생성하러가기
      </S.CreateRoadmapButton>
    </S.RoadmapList>
  );
};

export default RoadmapSearchResult;
