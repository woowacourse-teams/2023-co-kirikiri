import styled from 'styled-components';

export const GoalRoomGridContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(3, minmax(28rem, 1fr));
  grid-template-rows: repeat(28rem, 1fr);
  gap: 2rem;

  min-height: 90vh;
  margin-top: 1.5rem;
  padding: 1rem 0;
`;

export const DashBoardSection = styled.section`
  display: flex;
  flex-direction: column;

  height: 100%;
  min-height: 25rem;
  padding: 1rem;

  background: ${({ theme }) => theme.colors.gray100};
  border-radius: 10px;
  box-shadow: ${({ theme }) => theme.shadows.main};

  & > div:nth-child(2) {
    flex: 1;

    margin-top: 1rem;
    padding: 1rem;

    background: ${({ theme }) => theme.colors.white};
    border-radius: 10px;
  }

  & > div:first-child {
    ${({ theme }) => theme.fonts.h1};
    display: flex;
    align-items: center;
    justify-content: space-between;

    & > button {
      ${({ theme }) => theme.fonts.button2};
      display: flex;
      align-items: center;

      & > svg {
        width: 1.8rem;
      }
    }
  }
`;
