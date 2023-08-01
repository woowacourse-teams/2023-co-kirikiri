import PageSection from '@/components/goalRoomCreatePage/pageSection/PageSection';
import CreateGoalRoomForm from '@components/goalRoomCreatePage/createGoalRoomForm/CreateGoalRoomForm';
import * as S from './GoalRoomCreatePage.styles';

const GoalRoomCreatePage = () => {
  return (
    <S.GoalRoomCreatePage>
      <S.PageTitle>골룸을 생성해주세요!</S.PageTitle>
      <PageSection title='선택된 로드맵' description='생성할 골룸이 연결된 로드맵입니다'>
        <S.RoadmapInfo>&gt; 자바스크립트 90일 단기완성 / 생성자: 고루루</S.RoadmapInfo>
      </PageSection>
      <CreateGoalRoomForm />
    </S.GoalRoomCreatePage>
  );
};

export default GoalRoomCreatePage;
