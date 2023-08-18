import * as S from './GoalRoomDashboardBarricade.styles';
import { GoalRoomRecruitmentStatus } from '@myTypes/goalRoom/internal';
import { useStartGoalRoom } from '@hooks/queries/goalRoom';

type GoalRoomDashboardBarricadeProps = {
  isLeader: boolean;
  status: GoalRoomRecruitmentStatus;
  startDate: string;
  goalroomId: string;
};

const GoalRoomDashboardBarricade = ({
  isLeader,
  status,
  startDate,
  goalroomId,
}: GoalRoomDashboardBarricadeProps) => {
  const { startGoalRoom } = useStartGoalRoom(goalroomId);

  const goalRoomStartButtonHandler = () => {
    startGoalRoom();
  };

  const canStartGoalRoom =
    (status === 'RECRUIT_COMPLETED' || status === 'RECRUITING') && isLeader;

  return (
    <S.GoalRoomStatusBarricade>
      <S.GoalRoomBarricadeWrapper>
        <p>아쉽지만 현재 활성화된 골룸이 아닙니다.</p>
        {canStartGoalRoom && (
          <>
            <p>시작 예정일 {startDate}</p>
            <button onClick={goalRoomStartButtonHandler}>지금 골룸 시작하기</button>
          </>
        )}
      </S.GoalRoomBarricadeWrapper>
    </S.GoalRoomStatusBarricade>
  );
};

export default GoalRoomDashboardBarricade;
