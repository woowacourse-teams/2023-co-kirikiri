import RecommendRoadmaps from '@components/roadmapListPage/recommandRoadmaps/RecommendRoadmaps';
import RoadmapListView from '@components/roadmapListPage/roadmapListView/RoadmapListView';
import * as S from './RoadmapListPage.styles';

const RoadmapListPage = () => {
  return (
    <S.RoadmapListPage>
      <RecommendRoadmaps />
      <RoadmapListView />
    </S.RoadmapListPage>
  );
};

export default RoadmapListPage;
