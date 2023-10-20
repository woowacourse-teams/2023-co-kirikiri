import PageSection from '../pageSection/PageSection';
import CreateGoalRoomForm from '../createGoalRoomForm/CreateGoalRoomForm';
import useValidParams from '@hooks/_common/useValidParams';
import * as S from './CreateGoalRoom.styles';
import { useRoadmapDetail } from '@hooks/queries/roadmap';

const CreateGoalRoom = () => {
  const { id: roadmapId } = useValidParams();
  const { roadmapInfo } = useRoadmapDetail(Number(roadmapId));

  return (
    <div>
      <PageSection title='선택된 로드맵' description='생성할 모임이 연결된 로드맵입니다'>
        <S.RoadmapInfo>
          &gt; {roadmapInfo.roadmapTitle} / 생성자: {roadmapInfo.creator.name}
        </S.RoadmapInfo>
      </PageSection>
      <CreateGoalRoomForm
        roadmapContentId={Number(roadmapInfo.roadmapId)}
        nodes={roadmapInfo.content.nodes || []}
      />
    </div>
  );
};

export default CreateGoalRoom;
