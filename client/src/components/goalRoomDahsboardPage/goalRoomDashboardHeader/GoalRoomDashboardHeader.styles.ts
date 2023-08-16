import styled from 'styled-components';
import { BackDrop } from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/GoalRoomDashboardTodo.styles';

export const GoalRoomDashboardTitle = styled.h1`
  ${({ theme }) => theme.fonts.nav_title};
  margin: 3rem 0;
`;

export const GoalRoomLabel = styled.div`
  ${({ theme }) => theme.fonts.description_4};
  display: flex;
  align-items: center;

  padding: 0.5rem;
  margin-top: 1rem;
  max-width: 23rem;

  border-radius: 10px;
  background: ${({ theme }) => theme.colors.main_middle};
  box-shadow: ${({ theme }) => theme.shadows.text};

  & > svg {
    width: 3rem;
  }
`;

export const LabelButton = styled.button`
  ${({ theme }) => theme.fonts.description_1};
  background: ${({ theme }) => theme.colors.main_dark};
  border-radius: 10px;
  margin-left: 0.5rem;
  transition: transform 0.2s ease-in-out;

  &:hover {
    transform: scale(1.03);
  }
`;

export const ModalBackdrop = styled(BackDrop)``;
