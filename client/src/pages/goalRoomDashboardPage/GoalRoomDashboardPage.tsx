import GoalRoomDashboardContent from '@components/goalRoomDahsboardPage/goalRoomDashboardContent/GoalRoomDashboardContent';
import GoalRoomDashboardProvider from '@components/_providers/GoalRoomDashboardProvider';
import AsyncBoundary from '@/components/_common/errorBoundary/AsyncBoundary';

const GoalRoomDashboardPage = () => {
  return (
    <AsyncBoundary>
      <GoalRoomDashboardProvider>
        <GoalRoomDashboardContent />
      </GoalRoomDashboardProvider>
    </AsyncBoundary>
  );
};

export default GoalRoomDashboardPage;
