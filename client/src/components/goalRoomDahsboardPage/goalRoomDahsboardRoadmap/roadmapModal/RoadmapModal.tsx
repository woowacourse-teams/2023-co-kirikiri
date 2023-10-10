import * as S from './RoadmapModal.styles';
import Slider from '@components/_common/slider/Slider';
import { RoadmapDetailType } from '@myTypes/roadmap/internal';
import NodeContent from '@components/roadmapDetailPage/nodeContent/NodeContent';

type RoadmapModalProps = {
  roadmapInfo: RoadmapDetailType;
};

const RoadmapModal = ({ roadmapInfo }: RoadmapModalProps) => {
  return (
    <S.RoadmapModalWrapper>
      <S.RoadmapHeader>로드맵</S.RoadmapHeader>
      <div>
        <Slider>
          {roadmapInfo.content.nodes.map((node, index) => (
            <NodeContent key={node.id} node={node} index={index} />
          ))}
        </Slider>
      </div>
    </S.RoadmapModalWrapper>
  );
};

export default RoadmapModal;
