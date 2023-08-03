import * as S from './goalRoomList.styles';
import GoalRoomItem from './GoalRoomItem';
import { useGoalRoomList } from '@/hooks/queries/goalRoom';
import useValidParams from '@/hooks/_common/useValidParams';
import GoalRoomDetailDialog from '../goalRoomDetail/GoalRoomDetailDialog';

const GoalRoomList = () => {
  const { id } = useValidParams<{ id: string }>();
  const { goalRoomList } = useGoalRoomList({ roadmapId: Number(id) });

  return (
    <S.ListContainer>
      <S.FilterBar>
        <p>모집중인 골룸 {goalRoomList.length}개</p>
        <p>마감 임박순</p>
      </S.FilterBar>
      <S.ListWrapper>
        {goalRoomList.map((goalRoomInfo) => {
          return <GoalRoomItem {...goalRoomInfo} />;
        })}
      </S.ListWrapper>
      <GoalRoomDetailDialog />
    </S.ListContainer>
  );
};

export default GoalRoomList;
