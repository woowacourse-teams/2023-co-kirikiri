import * as S from './goalRoomList.styles';
import GoalRoomItem from './GoalRoomItem';
import { useGoalRoomList } from '@/hooks/queries/goalRoom';
import useValidParams from '@/hooks/_common/useValidParams';
import { useNavigate } from 'react-router-dom';

const GoalRoomList = () => {
  const { id } = useValidParams<{ id: string }>();
  const { goalRoomList } = useGoalRoomList({ roadmapId: Number(id) });
  const navigate = useNavigate();

  const moveCreateGoalRoomPage = () => {
    navigate(`/roadmap/${Number(id)}/goalroom-create`);
  };
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
        <S.CreateGoalRoomButton onClick={moveCreateGoalRoomPage}>
          골룸 생성하러 가기
        </S.CreateGoalRoomButton>
      </S.ListWrapper>
    </S.ListContainer>
  );
};

export default GoalRoomList;
