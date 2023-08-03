import GoalRoomDashboardHeader from '@components/goalRoomDahsboardPage/goalRoomDashboardHeader/GoalRoomDashboardHeader';
import GoalRoomDashboardChat from '@components/goalRoomDahsboardPage/goalRoomDashboardChat/GoalRoomDashboardChat';
import GoalRoomDashboardTodo from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/GoalRoomDashboardTodo';
import GoalRoomDashboardRoadmap from '@components/goalRoomDahsboardPage/goalRoomDahsboardRoadmap/GoalRoomDashboardRoadmap';
import GoalRoomDashboardCalender from '@components/goalRoomDahsboardPage/goalRoomDashboardCalender/GoalRoomDashboardCalender';
import GoalRoomCertificationFeed from '@components/goalRoomDahsboardPage/goalRoomCertificationFeed/GoalRoomCertificationFeed';

import { useFetchGoalRoom } from '@hooks/queries/goalRoom';

import * as S from './GoalRoomDashboardContent.styles';
import { Suspense } from 'react';
import Spinner from '@components/_common/spinner/Spinner';
import { useGoalRoomDashboardContext } from '@/context/goalRoomDashboardContext';

export type GoalRoomDashboardContentParams = {
  goalroomId: string;
};

const GoalRoomDashboardContent = () => {
  const { goalroomId } = useGoalRoomDashboardContext();

  const { goalRoom } = useFetchGoalRoom(goalroomId);

  return (
    <Suspense fallback={<Spinner />}>
      <div>
        <GoalRoomDashboardHeader goalRoomData={goalRoom} />
        <S.GoalRoomGridContainer>
          <GoalRoomDashboardChat />
          <GoalRoomDashboardTodo goalRoomData={goalRoom} />
          <GoalRoomDashboardRoadmap />
          <GoalRoomDashboardCalender />
          <GoalRoomCertificationFeed goalRoomData={goalRoom} />
        </S.GoalRoomGridContainer>
      </div>
    </Suspense>
  );
};

export default GoalRoomDashboardContent;
