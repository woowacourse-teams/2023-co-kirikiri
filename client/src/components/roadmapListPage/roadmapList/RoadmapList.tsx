import { useRoadmapList } from '@hooks/queries/roadmap';
import RoadmapItem from '@components/_common/roadmapItem/RoadmapItem';
import * as S from './RoadmapList.styles';
import { SelectedCategoryId } from '@myTypes/roadmap/internal';
import { useNavigate } from 'react-router-dom';

type RoadmapListProps = {
  selectedCategoryId: SelectedCategoryId;
};

const RoadmapList = ({ selectedCategoryId }: RoadmapListProps) => {
  const roadmapList = useRoadmapList(selectedCategoryId);
  const navigate = useNavigate();

  const moveRoadmapCreatePage = () => {
    navigate('/roadmap-create');
  };

  return (
    <S.RoadmapList aria-label='로드맵 목록'>
      {roadmapList?.map((item) => (
        <RoadmapItem key={item.roadmapId} item={item} roadmapId={item.roadmapId} />
      ))}
      <S.CreateRoadmapButton onClick={moveRoadmapCreatePage}>
        로드맵 생성하러가기
      </S.CreateRoadmapButton>
    </S.RoadmapList>
  );
};

export default RoadmapList;
