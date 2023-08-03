import { PropsWithChildren } from 'react';
import useValidParams from '@hooks/_common/useValidParams';
import { GoalRoomDashboardContentParams } from '@components/goalRoomDahsboardPage/goalRoomDashboardContent/GoalRoomDashboardContent';
import { goalRoomDashboardContext } from '@/context/goalRoomDashboardContext';

const GoalRoomDashboardProvider = ({ children }: PropsWithChildren) => {
  const { goalroomId } = useValidParams<GoalRoomDashboardContentParams>();

  return (
    <goalRoomDashboardContext.Provider value={{ goalroomId }}>
      {children}
    </goalRoomDashboardContext.Provider>
  );
};

export default GoalRoomDashboardProvider;
