import * as S from './goalRoomList.styles';
import GoalRoomItem from './GoalRoomItem';
import { useGoalRoomList } from '@/hooks/queries/goalRoom';
import useValidParams from '@/hooks/_common/useValidParams';

const GoalRoomList = () => {
  const { id } = useValidParams<{ id: string }>();
  const { goalRoomList } = useGoalRoomList({ roadmapId: Number(id) });

  return (
    <S.ListContainer role='main' aria-label='골룸 리스트'>
      <S.FilterBar>
        <p aria-live='polite'>모집중인 골룸 {goalRoomList.length}개</p>
        <p aria-live='polite'>마감 임박순</p>
      </S.FilterBar>
      <S.ListWrapper role='list' aria-label='골룸 리스트'>
        {goalRoomList.map((goalRoomInfo) => {
          return <GoalRoomItem {...goalRoomInfo} />;
        })}
      </S.ListWrapper>
    </S.ListContainer>
  );
};

export default GoalRoomList;
