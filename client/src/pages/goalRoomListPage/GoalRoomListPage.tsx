import GoalRoomList from '@/components/goalRoomListPage/goalRoomList/GoalRoomList';
import * as S from './goalRoomListPage.styles';
import AsyncBoundary from '@/components/_common/errorBoundary/AsyncBoundary';

const GoalRoomListPage = () => {
  return (
    <S.Container>
      <AsyncBoundary>
        <GoalRoomList />
      </AsyncBoundary>
    </S.Container>
  );
};

export default GoalRoomListPage;
