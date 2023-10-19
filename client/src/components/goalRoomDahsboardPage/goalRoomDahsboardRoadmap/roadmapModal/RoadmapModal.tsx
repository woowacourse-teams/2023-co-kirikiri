import * as S from './RoadmapModal.styles';
import Slider from '@components/_common/slider/Slider';
import NodeContent from '@components/roadmapDetailPage/nodeContent/NodeContent';
import { useGoalRoomNodeList } from '@hooks/queries/goalRoom';

type RoadmapModalProps = {
  goalroomId: string;
};

const RoadmapModal = ({ goalroomId }: RoadmapModalProps) => {
  const { goalRoomNodeList } = useGoalRoomNodeList(goalroomId);

  return (
    <S.RoadmapModalWrapper>
      <S.RoadmapHeader>로드맵</S.RoadmapHeader>
      <div>
        <Slider>
          {goalRoomNodeList.map((node, index) => (
            <NodeContent key={node.id} node={node} index={index} />
          ))}
        </Slider>
      </div>
    </S.RoadmapModalWrapper>
  );
};

export default RoadmapModal;
