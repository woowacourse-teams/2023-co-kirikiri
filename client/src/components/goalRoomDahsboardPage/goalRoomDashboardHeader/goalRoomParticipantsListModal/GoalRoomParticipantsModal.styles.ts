import styled from 'styled-components';
import {
  ModalHeader,
  ModalWrapper,
} from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/todoModal/TodoModal.styles';

export const GoalRoomParticipantsModalWrapper = styled(ModalWrapper)`
  width: 30rem;
`;

export const ParticipantsHeader = styled(ModalHeader)``;

export const SelectWrapper = styled.div`
  margin-bottom: 1.5rem;
`;

export const Select = styled.select`
  width: 100%;
  padding: 0.8rem;

  font-size: 1.4rem;

  appearance: none;
  border: 1px solid ${({ theme }) => theme.colors.gray200};
  border-radius: 5px;
`;

export const ParticipantWrapper = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
`;

export const ParticipantImage = styled.img`
  width: 4rem;
  height: 4rem;
  margin-right: 1rem;
`;
export const ParticipantName = styled.span`
  ${({ theme }) => theme.fonts.description4}
`;
