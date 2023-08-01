import { DashBoardSection } from '@components/goalRoomDahsboardPage/goalRoomDashboardContent/GoalRoomDashboardContent.styles';
import styled from 'styled-components';

export const CertificationFeedWrapper = styled(DashBoardSection)``;

export const ImageGrid = styled.div`
  display: grid;
  grid-gap: 1rem;
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);

  max-width: 50rem;
  max-height: 50rem;
  margin: 0 auto;
`;

export const StyledImage = styled.img`
  width: 100%;
  height: 100%;

  object-fit: cover;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24);

  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);

  &:hover {
    box-shadow: 0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22);
  }
`;
