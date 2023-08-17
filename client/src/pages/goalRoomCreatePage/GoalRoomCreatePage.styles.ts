import { styled } from 'styled-components';

export const GoalRoomCreatePage = styled.div`
  padding: 2rem;
`;

export const PageTitle = styled.div`
  ${({ theme }) => theme.fonts.title_large}
  display: flex;
  justify-content: center;
  margin-bottom: 4rem;
`;

export const RoadmapInfo = styled.div`
  margin-top: 1.8rem;
  ${({ theme }) => theme.fonts.description4}
`;
