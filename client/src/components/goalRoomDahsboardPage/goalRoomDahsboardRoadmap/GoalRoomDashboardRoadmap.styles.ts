import { DashBoardSection } from '@components/goalRoomDahsboardPage/goalRoomDashboardContent/GoalRoomDashboardContent.styles';
import styled from 'styled-components';

export const RoadmapWrapper = styled(DashBoardSection)`
  grid-row: span 2;
`;

export const RoadmapContainer = styled.ul`
  overflow: scroll;
  overflow-y: scroll;
  display: flex;
  flex-direction: column;
  gap: 4rem;
  align-items: center;

  min-height: 50rem;
  margin-top: 3.3rem;
  padding-top: 3rem;

  background-color: ${({ theme }) => theme.colors.white};
  border-radius: 20px;
`;

export const NodeContainer = styled.li`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-around;

  width: 27.5rem;
  height: 10rem;
  padding: 1.2rem 1rem;

  border: 0.2rem solid ${({ theme }) => theme.colors.main_dark};
  border-radius: 20px;
`;

export const NodeTitle = styled.div`
  ${({ theme }) => theme.fonts.button1}
`;

export const NodePeriod = styled.div`
  ${({ theme }) => theme.fonts.button1}
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const FeedCount = styled.div`
  ${({ theme }) => theme.fonts.description4}
  color: ${({ theme }) => theme.colors.gray300};
`;
