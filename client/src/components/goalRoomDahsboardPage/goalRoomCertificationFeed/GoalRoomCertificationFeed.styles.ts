import { DashBoardSection } from '@components/goalRoomDahsboardPage/goalRoomDashboardContent/GoalRoomDashboardContent.styles';
import styled from 'styled-components';
import {
  BackDrop,
  CountBox,
  TitleWrapper,
} from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/GoalRoomDashboardTodo.styles';

export const CertificationFeedWrapper = styled(DashBoardSection)``;

export const ImageGrid = styled.div`
  display: grid;
  grid-gap: 1rem;
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);

  min-width: 100%;
  max-width: 50rem;
  max-height: 50rem;
  margin: 0 auto;
`;

export const StyledImage = styled.img`
  width: 100%;
  height: 100%;

  object-fit: cover;
  box-shadow: ${({ theme }) => theme.shadows.threeD};

  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);

  &:hover {
    box-shadow: ${({ theme }) => theme.shadows.threeDHovered};
  }
`;

export const ModalBackdrop = styled(BackDrop)``;

export const CertificationTitleWrapper = styled(TitleWrapper)``;

export const CertificationCountBox = styled(CountBox)``;
