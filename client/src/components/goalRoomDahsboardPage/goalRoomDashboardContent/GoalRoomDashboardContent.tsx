import GoalRoomDashboardHeader from '@components/goalRoomDahsboardPage/goalRoomDashboardHeader/GoalRoomDashboardHeader';
import GoalRoomDashboardChat from '@components/goalRoomDahsboardPage/goalRoomDashboardChat/GoalRoomDashboardChat';
import GoalRoomDashboardTodo from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/GoalRoomDashboardTodo';
import GoalRoomDashboardRoadmap from '@components/goalRoomDahsboardPage/goalRoomDahsboardRoadmap/GoalRoomDashboardRoadmap';
import GoalRoomDashboardCalender from '@components/goalRoomDahsboardPage/goalRoomDashboardCalender/GoalRoomDashboardCalender';
import GoalRoomCertificationFeed from '@components/goalRoomDahsboardPage/goalRoomCertificationFeed/GoalRoomCertificationFeed';

import { useFetchGoalRoom } from '@hooks/queries/goalRoom';

import * as S from './GoalRoomDashboardContent.styles';
import useValidParams from '@hooks/_common/useValidParams';

type GoalRoomDashboardContentParams = {
  goalroomId: string;
};

const GoalRoomDashboardContent = () => {
  const { goalroomId } = useValidParams<GoalRoomDashboardContentParams>();

  const { goalRoom } = useFetchGoalRoom(goalroomId);

  return (
    <div>
      <GoalRoomDashboardHeader
        name={goalRoom.name}
        status={goalRoom.status}
        currentMemberCount={goalRoom.currentMemberCount}
        initMemberCount={goalRoom.initMemberCount}
        startDate={goalRoom.startDate}
        endDate={goalRoom.endDate}
      />
      <S.GoalRoomGridContainer>
        <GoalRoomDashboardChat />
        <GoalRoomDashboardTodo />
        <GoalRoomDashboardRoadmap />
        <GoalRoomDashboardCalender />
        <GoalRoomCertificationFeed />
      </S.GoalRoomGridContainer>
    </div>
  );
};

export default GoalRoomDashboardContent;
