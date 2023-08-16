import styled from 'styled-components';
import { ElephantImage } from '@pages/loginPage/LoginPage.styles';

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

export const Elephant = styled(ElephantImage)`
  position: relative;
  width: 30rem;
  height: 30rem;
`;

export const MainPageDesc = styled.p`
  ${({ theme }) => theme.fonts.h2};
  text-align: center;
  margin: 3rem 0 1rem 0;
`;

export const MainPageTitle = styled.p`
  ${({ theme }) => theme.fonts.h1};
  color: ${({ theme }) => theme.colors.main_dark};
  text-align: center;
`;

export const GoalRoomListButton = styled.button`
  ${({ theme }) => theme.fonts.description3};
  margin-top: 2rem;

  background: ${({ theme }) => theme.colors.main_middle};

  width: 20rem;
  height: 3rem;

  border-radius: 10px;

  transition: transform 0.2s ease-in-out;

  &:hover {
    transform: scale(1.03);
  }
`;
