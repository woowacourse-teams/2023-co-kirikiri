import GoalRoomDashboardHeader from '@components/goalRoomDahsboardPage/goalRoomDashboardHeader/GoalRoomDashboardHeader';
import GoalRoomDashboardChat from '@components/goalRoomDahsboardPage/goalRoomDashboardChat/GoalRoomDashboardChat';
import GoalRoomDashboardTodo from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/GoalRoomDashboardTodo';
import GoalRoomDashboardRoadmap from '@components/goalRoomDahsboardPage/goalRoomDahsboardRoadmap/GoalRoomDashboardRoadmap';
import GoalRoomDashboardCalender from '@components/goalRoomDahsboardPage/goalRoomDashboardCalender/GoalRoomDashboardCalender';
import GoalRoomCertificationFeed from '@components/goalRoomDahsboardPage/goalRoomCertificationFeed/GoalRoomCertificationFeed';

import * as S from './GoalRoomDashboardContent.styles';

const GoalRoomDashboardContent = () => {
  return (
    <div>
      <GoalRoomDashboardHeader />
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
