import {
  DialogBackdrop,
  DialogBox,
  DialogContent,
} from '@/components/_common/dialog/dialog';
import * as S from './goalRoomDetailDialog.styles';
import GoalRoomDetailDialogContent from './GoalRoomDetailDialogContent';

const GoalRoomDetailDialog = () => {
  return (
    <DialogBox defaultOpen>
      <DialogBackdrop asChild>
        <S.BackDrop>
          <DialogContent>
            <GoalRoomDetailDialogContent />
          </DialogContent>
        </S.BackDrop>
      </DialogBackdrop>
      <S.EnterGoalRoomButton>17일동안 골룸 참여하기</S.EnterGoalRoomButton>
    </DialogBox>
  );
};

export default GoalRoomDetailDialog;
