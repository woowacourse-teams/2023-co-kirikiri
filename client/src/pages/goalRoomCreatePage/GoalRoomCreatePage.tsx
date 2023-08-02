import CreateGoalRoom from '@components/goalRoomCreatePage/createGoalRoom/CreateGoalRoom';
import * as S from './GoalRoomCreatePage.styles';

const GoalRoomCreatePage = () => {
  return (
    <S.GoalRoomCreatePage>
      <S.PageTitle>골룸을 생성해주세요!</S.PageTitle>
      <CreateGoalRoom />
    </S.GoalRoomCreatePage>
  );
};

export default GoalRoomCreatePage;
