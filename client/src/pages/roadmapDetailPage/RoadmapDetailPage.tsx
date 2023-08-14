import { Suspense } from 'react';
import RoadmapDetail from '@components/roadmapDetailPage/roadmapDetail/RoadmapDetail';
import Spinner from '@components/_common/spinner/Spinner';

const RoadmapDetailPage = () => {
  return (
    <Suspense fallback={<Spinner />}>
      <RoadmapDetail />
    </Suspense>
  );
};

export default RoadmapDetailPage;
