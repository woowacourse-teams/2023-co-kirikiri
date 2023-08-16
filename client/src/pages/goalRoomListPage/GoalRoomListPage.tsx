import GoalRoomList from '@/components/goalRoomListPage/goalRoomList/GoalRoomList';
import { Suspense } from 'react';
import * as S from './goalRoomListPage.styles';
import Spinner from '@components/_common/spinner/Spinner';

const GoalRoomListPage = () => {
  return (
    <S.Container>
      <Suspense fallback={<Spinner />}>
        <GoalRoomList />
      </Suspense>
    </S.Container>
  );
};

export default GoalRoomListPage;
