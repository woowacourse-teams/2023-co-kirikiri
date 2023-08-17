import {
  ParticipantImage,
  ParticipantName,
} from '@components/goalRoomDahsboardPage/goalRoomDashboardHeader/goalRoomParticipantsListModal/GoalRoomParticipantsModal.styles';
import styled from 'styled-components';
import {
  ModalHeader,
  ModalWrapper,
} from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/todoModal/TodoModal.styles';

export const RankingUserWrapper = styled(ModalWrapper)`
  width: 35rem;
`;

export const RankingUserImage = styled(ParticipantImage)``;

export const RankingUserNickname = styled(ParticipantName)``;

export const RankingTitle = styled(ModalHeader)``;

export const RankingRow = styled.li`
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 2rem;
`;
