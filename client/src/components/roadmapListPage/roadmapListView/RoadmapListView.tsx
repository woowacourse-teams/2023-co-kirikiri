import RoadmapItem from '@components/_common/roadmapItem/RoadmapItem';
import Categories from '../categories/Categories';

import * as S from './RoadmapListView.styles';
import { useRoadmapList } from '@/hooks/queries/roadmap';

const RoadmapListView = () => {
  const {
    data: { data: roadmapList },
  } = useRoadmapList();
  console.log(roadmapList);
  return (
    <S.RoadmapListView>
      <Categories />
      <S.RoadmapList>
        {roadmapList.map((item) => (
          <RoadmapItem key={item.roadmapId} item={item} />
        ))}
      </S.RoadmapList>
    </S.RoadmapListView>
  );
};

export default RoadmapListView;
