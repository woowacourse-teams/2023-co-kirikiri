import { Dialog } from 'ck-util-components';
import * as S from './goalRoomDetailDialog.styles';
import GoalRoomDetailDialogContent from './GoalRoomDetailDialogContent';

type GoalRoomDetailDialogProps = {
  goalRoomId: number;
};

const GoalRoomDetailDialog = ({ goalRoomId }: GoalRoomDetailDialogProps) => {
  return (
    <Dialog>
      <Dialog.Trigger asChild>
        <S.DetailButton>자세히 보기</S.DetailButton>
      </Dialog.Trigger>
      <Dialog.BackDrop asChild>
        <S.BackDrop />
      </Dialog.BackDrop>
      <Dialog.Content>
        <GoalRoomDetailDialogContent goalRoomId={goalRoomId} />
      </Dialog.Content>
    </Dialog>
  );
};

export default GoalRoomDetailDialog;
