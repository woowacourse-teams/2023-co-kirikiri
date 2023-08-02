import GoalRoomDashboardHeader from '@components/goalRoomDahsboardPage/goalRoomDashboardHeader/GoalRoomDashboardHeader';
import GoalRoomDashboardChat from '@components/goalRoomDahsboardPage/goalRoomDashboardChat/GoalRoomDashboardChat';
import GoalRoomDashboardTodo from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/GoalRoomDashboardTodo';
import GoalRoomDashboardRoadmap from '@components/goalRoomDahsboardPage/goalRoomDahsboardRoadmap/GoalRoomDashboardRoadmap';
import GoalRoomDashboardCalender from '@components/goalRoomDahsboardPage/goalRoomDashboardCalender/GoalRoomDashboardCalender';
import GoalRoomCertificationFeed from '@components/goalRoomDahsboardPage/goalRoomCertificationFeed/GoalRoomCertificationFeed';

import { useFetchGoalRoom } from '@hooks/queries/goalRoom';

import * as S from './GoalRoomDashboardContent.styles';
import useValidParams from '@hooks/_common/useValidParams';

export type GoalRoomDashboardContentParams = {
  goalroomId: string;
};

const GoalRoomDashboardContent = () => {
  const { goalroomId } = useValidParams<GoalRoomDashboardContentParams>();

  const { goalRoom } = useFetchGoalRoom(goalroomId);

  return (
    <div>
      <GoalRoomDashboardHeader goalRoomData={goalRoom} />
      <S.GoalRoomGridContainer>
        <GoalRoomDashboardChat />
        <GoalRoomDashboardTodo goalRoomData={goalRoom} goalRoomId={goalroomId} />
        <GoalRoomDashboardRoadmap />
        <GoalRoomDashboardCalender />
        <GoalRoomCertificationFeed goalRoomData={goalRoom} />
      </S.GoalRoomGridContainer>
    </div>
  );
};

export default GoalRoomDashboardContent;
