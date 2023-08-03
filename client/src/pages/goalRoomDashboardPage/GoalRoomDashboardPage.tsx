import GoalRoomDashboardContent from '@components/goalRoomDahsboardPage/goalRoomDashboardContent/GoalRoomDashboardContent';
import GoalRoomDashboardProvider from '@components/_providers/GoalRoomDashboardProvider';

const GoalRoomDashboardPage = () => {
  return (
    <GoalRoomDashboardProvider>
      <GoalRoomDashboardContent />
    </GoalRoomDashboardProvider>
  );
};

export default GoalRoomDashboardPage;
