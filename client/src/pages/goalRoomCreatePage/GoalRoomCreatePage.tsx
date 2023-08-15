import { Suspense } from 'react';
import CreateGoalRoom from '@components/goalRoomCreatePage/createGoalRoom/CreateGoalRoom';
import Spinner from '@components/_common/spinner/Spinner';
import * as S from './GoalRoomCreatePage.styles';

const GoalRoomCreatePage = () => {
  return (
    <S.GoalRoomCreatePage>
      <S.PageTitle>골룸을 생성해주세요!</S.PageTitle>
      <Suspense fallback={<Spinner />}>
        <CreateGoalRoom />
      </Suspense>
    </S.GoalRoomCreatePage>
  );
};

export default GoalRoomCreatePage;
