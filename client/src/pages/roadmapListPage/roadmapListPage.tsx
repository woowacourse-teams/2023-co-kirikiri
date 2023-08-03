import { Suspense } from 'react';

import RoadmapListView from '@components/roadmapListPage/roadmapListView/RoadmapListView';
import Fallback from '@components/_common/fallback/Fallback';

import * as S from './RoadmapListPage.styles';

const RoadmapListPage = () => {
  return (
    <S.RoadmapListPage>
      <Suspense fallback={<Fallback />}>
        <RoadmapListView />
      </Suspense>
    </S.RoadmapListPage>
  );
};

export default RoadmapListPage;
