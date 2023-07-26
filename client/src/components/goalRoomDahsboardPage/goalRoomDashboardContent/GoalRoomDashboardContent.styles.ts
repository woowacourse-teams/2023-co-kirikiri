import styled from 'styled-components';

export const GoalRoomGridContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(28rem, 1fr));
  grid-template-rows: repeat(28rem, 1fr);
  gap: 2rem;

  min-height: 90vh;
  margin-top: 1.5rem;
  padding: 1rem 0;
`;

export const DashBoardSection = styled.section`
  min-height: 250px;
  padding: 1rem;
  border: 0.5rem solid ${({ theme }) => theme.colors.gray200};
  border-radius: 5px;

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
