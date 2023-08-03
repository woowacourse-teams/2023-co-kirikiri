import {
  DialogBackdrop,
  DialogBox,
  DialogContent,
} from '@/components/_common/dialog/dialog';
import * as S from './goalRoomDetailDialog.styles';
import GoalRoomDetailDialogContent from './GoalRoomDetailDialogContent';

type GoalRoomDetailDialogProps = {
  closeGoalroomDetail: () => void;
  goalRoomId: number;
};

const GoalRoomDetailDialog = ({
  closeGoalroomDetail,
  goalRoomId,
}: GoalRoomDetailDialogProps) => {
  return (
    <DialogBox defaultOpen>
      <DialogBackdrop asChild>
        <S.BackDrop>
          <DialogContent>
            <GoalRoomDetailDialogContent
              closeGoalroomDetail={closeGoalroomDetail}
              goalRoomId={goalRoomId}
            />
            <S.EnterGoalRoomButton>17일동안 골룸 참여하기</S.EnterGoalRoomButton>
          </DialogContent>
        </S.BackDrop>
      </DialogBackdrop>
    </DialogBox>
  );
};

export default GoalRoomDetailDialog;
