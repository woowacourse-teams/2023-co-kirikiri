import * as S from './RoadmapModal.styles';
import Slider from '@components/_common/slider/Slider';
import NodeContent from '@components/roadmapDetailPage/nodeContent/NodeContent';
import { GoalRoomNodeType } from '@/myTypes/goalRoom/internal';

type RoadmapModalProps = {
  nodeList: GoalRoomNodeType[];
};

const RoadmapModal = ({ nodeList }: RoadmapModalProps) => {
  return (
    <S.RoadmapModalWrapper>
      <S.RoadmapHeader>로드맵</S.RoadmapHeader>
      <div>
        <Slider>
          {nodeList.map((node, index) => (
            <NodeContent key={node.id} node={node} index={index} />
          ))}
        </Slider>
      </div>
    </S.RoadmapModalWrapper>
  );
};

export default RoadmapModal;
