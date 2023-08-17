import SVGIcon from '@components/icons/SVGIcon';

import * as S from './GoalRoomDashboardHeader.styles';
import recruitmentStatus from '@constants/goalRoom/recruitmentStatus';
import { GoalRoomBrowseResponse } from '@myTypes/goalRoom/remote';
import {
  DialogBackdrop,
  DialogBox,
  DialogContent,
  DialogTrigger,
} from '@components/_common/dialog/dialog';
import GoalRoomParticipantsListModal from '@components/goalRoomDahsboardPage/goalRoomDashboardHeader/goalRoomParticipantsListModal/GoalRoomParticipantsListModal';

type GoalRoomDashboardHeaderProps = {
  goalRoomData: GoalRoomBrowseResponse;
};

const GoalRoomDashboardHeader = ({ goalRoomData }: GoalRoomDashboardHeaderProps) => {
  const { name, status, currentMemberCount, limitedMemberCount, startDate, endDate } =
    goalRoomData;

  return (
    <DialogBox>
      <header>
        <S.GoalRoomDashboardTitle>{name}</S.GoalRoomDashboardTitle>
        <S.GoalRoomLabel>
          <SVGIcon name='ITIcon' />
          <span>{recruitmentStatus[status]}</span>
        </S.GoalRoomLabel>
        <S.GoalRoomLabel>
          <SVGIcon name='PersonIcon' />
          <span>
            {currentMemberCount} / {limitedMemberCount} 명 참여 중
          </span>
          <DialogTrigger asChild>
            <S.LabelButton>전체 참여인원 보기</S.LabelButton>
          </DialogTrigger>
        </S.GoalRoomLabel>
        <S.GoalRoomLabel>
          <SVGIcon name='CalendarIcon' />
          <span>
            {startDate} ~ {endDate}
          </span>
        </S.GoalRoomLabel>
      </header>

      <DialogBackdrop asChild>
        <S.ModalBackdrop />
      </DialogBackdrop>

      <DialogContent>
        <GoalRoomParticipantsListModal />
      </DialogContent>
    </DialogBox>
  );
};

export default GoalRoomDashboardHeader;
