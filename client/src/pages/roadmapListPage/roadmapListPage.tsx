import { Suspense } from 'react';

import RecommendRoadmaps from '@components/roadmapListPage/recommandRoadmaps/RecommendRoadmaps';
import RoadmapListView from '@components/roadmapListPage/roadmapListView/RoadmapListView';
import Fallback from '@components/_common/fallback/Fallback';

import * as S from './RoadmapListPage.styles';

const RoadmapListPage = () => {
  return (
    <S.RoadmapListPage>
      <RecommendRoadmaps />
      <Suspense fallback={<Fallback />}>
        <RoadmapListView />
      </Suspense>
    </S.RoadmapListPage>
  );
};

export default RoadmapListPage;
