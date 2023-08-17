import {
  DialogBackdrop,
  DialogBox,
  DialogContent,
  DialogTrigger,
} from '@components/_common/dialog/dialog';
import * as S from './goalRoomDetailDialog.styles';
import GoalRoomDetailDialogContent from './GoalRoomDetailDialogContent';

type GoalRoomDetailDialogProps = {
  goalRoomId: number;
};

const GoalRoomDetailDialog = ({ goalRoomId }: GoalRoomDetailDialogProps) => {
  return (
    <DialogBox>
      <DialogTrigger asChild>
        <S.DetailButton>자세히 보기</S.DetailButton>
      </DialogTrigger>
      <DialogBackdrop asChild>
        <S.BackDrop />
      </DialogBackdrop>
      <DialogContent>
        <GoalRoomDetailDialogContent goalRoomId={goalRoomId} />
      </DialogContent>
    </DialogBox>
  );
};

export default GoalRoomDetailDialog;
