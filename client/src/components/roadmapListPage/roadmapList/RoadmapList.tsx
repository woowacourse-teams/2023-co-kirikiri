import { useRoadmapList } from '@hooks/queries/roadmap';
import RoadmapItem from '@components/_common/roadmapItem/RoadmapItem';
import * as S from './RoadmapList.styles';
import { SelectedCategoryId } from '@myTypes/roadmap/internal';
import { useNavigate } from 'react-router-dom';
import { useInfiniteScroll } from '@hooks/_common/useInfiniteScroll';
import WavyLoading from '@/components/_common/wavyLoading/WavyLoading';

type RoadmapListProps = {
  selectedCategoryId: SelectedCategoryId;
};

const RoadmapList = ({ selectedCategoryId }: RoadmapListProps) => {
  const { roadmapListResponse, fetchNextPage } = useRoadmapList({
    categoryId: selectedCategoryId,
  });

  const loadMoreRef = useInfiniteScroll({
    hasNextPage: roadmapListResponse?.hasNext,
    fetchNextPage,
  });

  const navigate = useNavigate();

  const moveRoadmapCreatePage = () => {
    navigate('/roadmap-create');
  };

  return (
    <S.RoadmapList aria-label='로드맵 목록'>
      {roadmapListResponse.responses.map((item) => (
        <RoadmapItem key={item.roadmapId} item={item} roadmapId={item.roadmapId} />
      ))}
      {roadmapListResponse?.hasNext && <WavyLoading loadMoreRef={loadMoreRef} />}
      <S.CreateRoadmapButton onClick={moveRoadmapCreatePage}>
        로드맵 생성하러가기
      </S.CreateRoadmapButton>
    </S.RoadmapList>
  );
};

export default RoadmapList;
