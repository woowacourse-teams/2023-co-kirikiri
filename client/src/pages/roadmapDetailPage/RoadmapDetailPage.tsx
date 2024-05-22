import RoadmapDetail from '@components/roadmapDetailPage/roadmapDetail/RoadmapDetail';
import AsyncBoundary from '@/components/_common/errorBoundary/AsyncBoundary';

const RoadmapDetailPage = () => {
  return (
    <AsyncBoundary>
      <RoadmapDetail />
    </AsyncBoundary>
  );
};

export default RoadmapDetailPage;
