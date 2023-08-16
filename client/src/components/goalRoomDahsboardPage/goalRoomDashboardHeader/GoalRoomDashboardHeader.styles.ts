import styled from 'styled-components';

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
  max-width: 20rem;

  border-radius: 10px;
  background: ${({ theme }) => theme.colors.main_middle};
  box-shadow: ${({ theme }) => theme.shadows.text};

  & > svg {
    width: 3rem;
  }
`;

export const GoalRoomStartButton = styled.button`
  width: 15rem;
  height: 3rem;
  background: ${({ theme }) => theme.colors.main_dark};
  border-radius: 10px;
`;
