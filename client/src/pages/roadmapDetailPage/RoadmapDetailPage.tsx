import { Suspense } from 'react';
import Fallback from '@components/_common/fallback/Fallback';
import RoadmapDetail from '@components/roadmapDetailPage/roadmapDetail/RoadmapDetail';

const RoadmapDetailPage = () => {
  return (
    <Suspense fallback={<Fallback />}>
      <RoadmapDetail />
    </Suspense>
  );
};

export default RoadmapDetailPage;
