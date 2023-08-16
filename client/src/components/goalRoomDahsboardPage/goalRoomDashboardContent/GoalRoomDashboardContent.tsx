import GoalRoomDashboardHeader from '@components/goalRoomDahsboardPage/goalRoomDashboardHeader/GoalRoomDashboardHeader';
import GoalRoomDashboardTodo from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/GoalRoomDashboardTodo';
import GoalRoomDashboardRoadmap from '@components/goalRoomDahsboardPage/goalRoomDahsboardRoadmap/GoalRoomDashboardRoadmap';
import GoalRoomCertificationFeed from '@components/goalRoomDahsboardPage/goalRoomCertificationFeed/GoalRoomCertificationFeed';
import GoalRoomUserRanking from '@components/goalRoomDahsboardPage/goalRoomUserRanking/GoalRoomUserRanking';
import { useFetchGoalRoom } from '@hooks/queries/goalRoom';

import * as S from './GoalRoomDashboardContent.styles';
import { Suspense } from 'react';
import Spinner from '@components/_common/spinner/Spinner';
import { useGoalRoomDashboardContext } from '@/context/goalRoomDashboardContext';
import { useUserInfoContext } from '@components/_providers/UserInfoProvider';

export type GoalRoomDashboardContentParams = {
  goalroomId: string;
};

const GoalRoomDashboardContent = () => {
  const { goalroomId } = useGoalRoomDashboardContext();

  const { goalRoom } = useFetchGoalRoom(goalroomId);

  const { userInfo } = useUserInfoContext();

  const isLeader = userInfo.id === goalRoom.leaderId;

  return (
    <Suspense fallback={<Spinner />}>
      <div>
        <GoalRoomDashboardHeader goalRoomData={goalRoom} isLeader={isLeader} />
        <S.GoalRoomGridContainer>
          <GoalRoomDashboardTodo goalRoomData={goalRoom} isLeader={isLeader} />
          <GoalRoomDashboardRoadmap />
          <GoalRoomUserRanking />
          <GoalRoomCertificationFeed goalRoomData={goalRoom} />
        </S.GoalRoomGridContainer>
      </div>
    </Suspense>
  );
};

export default GoalRoomDashboardContent;
