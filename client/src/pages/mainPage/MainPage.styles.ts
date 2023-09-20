import styled from 'styled-components';

export const MainPageWrapper = styled.div`
  position: relative;

  display: flex;
  align-items: center;
  justify-content: center;

  width: 100%;
  height: 100vh;
`;

export const MainPageContent = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

export const Elephant = styled.img`
  position: relative;
  transform-style: preserve-3d;

  width: 30rem;
  height: 30rem;

  transition: transform 0.1s ease;
`;

export const MainPageDesc = styled.p`
  ${({ theme }) => theme.fonts.h1};
  text-align: center;
  margin: 3rem 0 1rem 0;
`;

export const MainPageTitle = styled.p`
  ${({ theme }) => theme.fonts.nav_title};
  color: ${({ theme }) => theme.colors.main_dark};
  text-align: center;
`;

export const GoalRoomListButton = styled.button`
  ${({ theme }) => theme.fonts.h2};
  margin-top: 2rem;

  background: ${({ theme }) => theme.colors.main_middle};

  color: ${({ theme }) => theme.colors.white};

  width: 30rem;
  height: 5rem;

  border-radius: 20px;

  transition: transform 0.2s ease-in-out;

  &:hover {
    transform: scale(1.03);
  }
`;
