import * as S from './GoalRoomParticipantsModal.styles';
import useValidParams from '@hooks/_common/useValidParams';
import { GoalRoomDashboardContentParams } from '@components/goalRoomDahsboardPage/goalRoomDashboardContent/GoalRoomDashboardContent';
import { useFetchGoalRoomParticipants } from '@hooks/queries/goalRoom';
import { ChangeEvent, useState } from 'react';
import { ParticipantsSortOrder } from '@myTypes/goalRoom/remote';

const GoalRoomParticipantsListModal = () => {
  const { goalroomId } = useValidParams<GoalRoomDashboardContentParams>();
  const [sortOrder, setSortOrder] = useState<ParticipantsSortOrder>('JOINED_DESC');

  const { goalRoomParticipants } = useFetchGoalRoomParticipants(goalroomId, sortOrder);

  const handleSortOrderChange = (e: ChangeEvent<HTMLSelectElement>) => {
    setSortOrder(e.target.value as ParticipantsSortOrder);
  };

  return (
    <S.GoalRoomParticipantsModalWrapper>
      <S.ParticipantsHeader>전체 참여인원</S.ParticipantsHeader>

      <S.SelectWrapper>
        <S.Select onChange={handleSortOrderChange} value={sortOrder}>
          <option value='JOINED_DESC'>최근 참여한 순</option>
          <option value='JOINED_ASC'>나중에 참여한 순</option>
          <option value='ACCOMPLISHMENT_RATE'>달성률 순</option>
        </S.Select>
      </S.SelectWrapper>

      {goalRoomParticipants.map((participant) => (
        <S.ParticipantWrapper key={participant.memberId}>
          <S.ParticipantImage src={participant.imagePath} alt='참여자 프로필 이미지' />
          <S.ParticipantName>{participant.nickname}</S.ParticipantName>
        </S.ParticipantWrapper>
      ))}
    </S.GoalRoomParticipantsModalWrapper>
  );
};

export default GoalRoomParticipantsListModal;
