import * as S from './RoadmapDetail.styles';
import useValidParams from '@hooks/_common/useValidParams';
import { useNavigate } from 'react-router-dom';
import { useRoadmapDetail } from '@hooks/queries/roadmap';

import Slider from '@components/_common/slider/Slider';
import NodeContent from '../nodeContent/NodeContent';
import ExtraInfo from '../extraInfo/ExtraInfo';
import Introduction from '../introduction/Introduction';

const RoadmapDetail = () => {
  const { id: roadmapId } = useValidParams<{ id: string }>();
  const navigate = useNavigate();
  const { roadmapInfo } = useRoadmapDetail(Number(roadmapId));

  return (
    <S.RoadmapDetail>
      <S.RoadmapInfo>
        <S.Title>{roadmapInfo.roadmapTitle}</S.Title>
        <S.Description>
          <Introduction roadmapInfo={roadmapInfo} />
          <ExtraInfo roadmapInfo={roadmapInfo} />
        </S.Description>
      </S.RoadmapInfo>
      <S.Buttons>
        <S.Button onClick={() => navigate(`/roadmap/${roadmapId}/goalroom-create`)}>
          모임 생성하기
        </S.Button>
        <div />
        <S.Button onClick={() => navigate(`/roadmap/${roadmapId}/goalroom-list`)}>
          진행중인 모임보기
        </S.Button>
      </S.Buttons>
      <Slider>
        {roadmapInfo.content.nodes.map((node, index) => (
          <NodeContent key={node.id} node={node} index={index} />
        ))}
      </Slider>
    </S.RoadmapDetail>
  );
};

export default RoadmapDetail;
