import GoalRoomList from '@/components/goalRoomListPage/goalRoomList/GoalRoomList';
import ListTitle from '@/components/goalRoomListPage/listTitle/ListTitle';
import Fallback from '@/components/_common/fallback/Fallback';
import { Suspense } from 'react';
import * as S from './goalRoomListPage.styles';

const GoalRoomListPage = () => {
  return (
    <S.Container>
      <ListTitle />
      <Suspense fallback={<Fallback />}>
        <GoalRoomList />
      </Suspense>
    </S.Container>
  );
};

export default GoalRoomListPage;
