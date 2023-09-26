import { DashBoardSection } from '@components/goalRoomDahsboardPage/goalRoomDashboardContent/GoalRoomDashboardContent.styles';
import styled, { keyframes } from 'styled-components';
import { BackDrop } from '@components/goalRoomListPage/goalRoomDetail/goalRoomDetailDialog.styles';

const bounce = keyframes`
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
`;
export const CalenderWrapper = styled(DashBoardSection)``;

export const PodiumWrapper = styled.div`
  position: relative;
  width: 100%;
  height: 100%;
`;

export const PodiumImage = styled.img`
  width: 100%;
  height: 50%;
  margin-top: 5rem;
  object-fit: cover;
`;

export const Participant = styled.div<{ position: number }>`
  position: absolute;
  top: 10%;
  animation: ${bounce} 2s infinite;

  ${({ position }) => {
    if (position === 0) return 'left: 40%; top: 2%; transform: translateX(-50%);';
    if (position === 1) return 'right: 4%; top: 7%;';
    if (position === 2) return 'left: 8%; top: 13%;';
    return '';
  }}
`;

export const Card = styled.div`
  ${({ theme }) => theme.fonts.description2};
  width: 100%;
  margin: 1rem 0;
  padding: 1rem;

  border: 1px solid #e0e0e0;
  border-radius: 0.5rem;
`;

export const UserInfoLabel = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;

  & > img {
    width: 5rem;
    height: 5rem;
    object-fit: cover;
  }
`;

export const ModalBackdrop = styled(BackDrop)``;
