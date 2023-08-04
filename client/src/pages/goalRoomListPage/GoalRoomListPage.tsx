import GoalRoomList from '@/components/goalRoomListPage/goalRoomList/GoalRoomList';
import Fallback from '@/components/_common/fallback/Fallback';
import { Suspense } from 'react';
import * as S from './goalRoomListPage.styles';

const GoalRoomListPage = () => {
  return (
    <S.Container>
      <Suspense fallback={<Fallback />}>
        <GoalRoomList />
      </Suspense>
    </S.Container>
  );
};

export default GoalRoomListPage;
