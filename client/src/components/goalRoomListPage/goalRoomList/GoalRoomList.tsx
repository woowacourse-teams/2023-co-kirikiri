// import { useGaolRoomList } from '@/hooks/queries/goalRoom';
import * as S from './goalRoomList.styles';
import GoalRoomItem from './GoalRoomItem';

const GoalRoomList = () => {
  //   const { data } = useGaolRoomList({});

  return (
    <S.ListContainer>
      <S.FilterBar>
        <p>모집중인 골룸 12개</p>
        <p>마감 임박순</p>
      </S.FilterBar>
      <S.ListWrapper>
        <GoalRoomItem />
        <GoalRoomItem />
        <GoalRoomItem />
        <GoalRoomItem />
      </S.ListWrapper>
    </S.ListContainer>
  );
};

export default GoalRoomList;
