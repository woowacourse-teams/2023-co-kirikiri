import GoalRoomDashboardContent from '@components/goalRoomDahsboardPage/goalRoomDashboardContent/GoalRoomDashboardContent';
import GoalRoomDashboardProvider from '@components/_providers/GoalRoomDashboardProvider';
import { Suspense } from 'react';
import Spinner from '@components/_common/spinner/Spinner';

const GoalRoomDashboardPage = () => {
  return (
    <Suspense fallback={<Spinner />}>
      <GoalRoomDashboardProvider>
        <GoalRoomDashboardContent />
      </GoalRoomDashboardProvider>
    </Suspense>
  );
};

export default GoalRoomDashboardPage;
