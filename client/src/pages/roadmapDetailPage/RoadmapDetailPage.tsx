import RoadmapBody from '@components/roadmapDetailPage/roadmapBody/RoadmapBody';
import * as S from './RoadmapDetail.styles';

const RoadmapDetailPage = () => {
  return (
    <S.RoadmapDetailPage>
      <S.RoadmapInfo>
        <RoadmapBody />
      </S.RoadmapInfo>
      <S.NodeList />
    </S.RoadmapDetailPage>
  );
};

export default RoadmapDetailPage;
