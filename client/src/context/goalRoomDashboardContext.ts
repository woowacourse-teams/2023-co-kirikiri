import { createContext, useContext } from 'react';
import { GoalRoomDashboardContextType } from '@myTypes/_common/dashboard';

export const goalRoomDashboardContext = createContext<GoalRoomDashboardContextType>({
  goalroomId: '',
});

export const useGoalRoomDashboardContext = () => useContext(goalRoomDashboardContext);
