import PageSection from '../pageSection/PageSection';
import CreateGoalRoomForm from '../createGoalRoomForm/CreateGoalRoomForm';
import useValidParams from '@hooks/_common/useValidParams';
import * as S from './CreateGoalRoom.styles';

const CreateGoalRoom = () => {
  const { id: roadmapContentId } = useValidParams();

  return (
    <div>
      <PageSection title='선택된 로드맵' description='생성할 골룸이 연결된 로드맵입니다'>
        <S.RoadmapInfo>&gt; 자바스크립트 90일 단기완성 / 생성자: 고루루</S.RoadmapInfo>
      </PageSection>
      <CreateGoalRoomForm roadmapContentId={Number(roadmapContentId)} />
    </div>
  );
};

export default CreateGoalRoom;
