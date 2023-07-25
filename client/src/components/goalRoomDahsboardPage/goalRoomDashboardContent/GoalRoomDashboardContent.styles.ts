import styled from 'styled-components';

export const GoalRoomGridContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  grid-template-rows: repeat(250px, 1fr);
  gap: 20px;

  min-height: 90vh;
  padding: 20px;
`;

export const DashBoardSection = styled.section`
  min-height: 250px;
  padding: 20px;
  border-radius: 5px;
`;
