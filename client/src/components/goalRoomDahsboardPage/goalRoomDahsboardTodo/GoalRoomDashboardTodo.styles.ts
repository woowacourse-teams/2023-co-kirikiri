import { DashBoardSection } from '@components/goalRoomDahsboardPage/goalRoomDashboardContent/GoalRoomDashboardContent.styles';
import styled from 'styled-components';
import { BackDrop } from '@components/goalRoomListPage/goalRoomDetail/goalRoomDetailDialog.styles';

export const TodoWrapper = styled(DashBoardSection)`
  grid-column: 1 /3;
`;

export const TitleWrapper = styled.div`
  display: flex;
  align-items: center;
`;

export const TodoContent = styled.ul`
  display: flex;
  flex: 1;
  flex-direction: column;
  justify-content: space-between;
`;

export const CountBox = styled.span`
  ${({ theme }) => theme.fonts.button1};
  display: flex;
  align-items: center;
  justify-content: center;

  width: 2.3rem;
  height: 2.3rem;
  margin-left: 1rem;

  background-color: ${({ theme }) => theme.colors.gray300};
  border-radius: 50%;
  box-shadow: ${({ theme }) => theme.shadows.main};
`;

export const DashboardBackDrop = styled(BackDrop)``;
