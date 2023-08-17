import styled from 'styled-components';

export const GoalRoomStatusBarricade = styled.div`
  position: absolute;
  z-index: 2;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;

  display: flex;
  align-items: center;
  justify-content: center;

  background: ${({ theme }) => theme.colors.backdrop};
`;

export const GoalRoomBarricadeWrapper = styled.div`
  ${({ theme }) => theme.fonts.description3};
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  width: 40%;
  height: 20%;
  margin-bottom: 3rem;

  background: ${({ theme }) => theme.colors.white};

  & > p {
    margin-bottom: 3rem;
  }

  & > button {
    width: 50%;
    padding: 1rem 0;
    background: ${({ theme }) => theme.colors.main_middle};
    border-radius: 10px;
  }
`;
