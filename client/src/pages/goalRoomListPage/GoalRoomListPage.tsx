import GoalRoomList from '@/components/goalRoomListPage/goalRoomList/GoalRoomList';
import ListTitle from '@/components/goalRoomListPage/listTitle/ListTitle';
import * as S from './goalRoomListPage.styles';

const GoalRoomListPage = () => {
  return (
    <S.Container>
      <ListTitle />
      <GoalRoomList />
    </S.Container>
  );
};

export default GoalRoomListPage;
