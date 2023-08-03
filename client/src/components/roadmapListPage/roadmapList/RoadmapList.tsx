import { useRoadmapList } from '@hooks/queries/roadmap';
import RoadmapItem from '@components/_common/roadmapItem/RoadmapItem';
import * as S from './RoadmapList.styles';
import { SelectedCategoryId } from '@myTypes/roadmap/internal';

type RoadmapListProps = {
  selectedCategoryId: SelectedCategoryId;
};

const RoadmapList = ({ selectedCategoryId }: RoadmapListProps) => {
  const roadmapList = useRoadmapList(selectedCategoryId);

  return (
    <S.RoadmapList>
      {roadmapList?.map((item) => (
        <RoadmapItem key={item.roadmapId} item={item} roadmapId={item.roadmapId} />
      ))}
    </S.RoadmapList>
  );
};

export default RoadmapList;
