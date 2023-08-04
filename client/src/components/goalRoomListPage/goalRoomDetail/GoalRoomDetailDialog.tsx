import {
  DialogBackdrop,
  DialogBox,
  DialogContent,
} from '@/components/_common/dialog/dialog';
import * as S from './goalRoomDetailDialog.styles';
import GoalRoomDetailDialogContent from './GoalRoomDetailDialogContent';
import { useNavigate } from 'react-router-dom';

type GoalRoomDetailDialogProps = {
  closeGoalroomDetail: () => void;
  goalRoomId: number;
  isJoined: boolean;
};

const GoalRoomDetailDialog = ({
  closeGoalroomDetail,
  goalRoomId,
  isJoined,
}: GoalRoomDetailDialogProps) => {
  console.log(isJoined);

  const navigate = useNavigate();

  const enterGoalRoom = () => {
    navigate(`/goalroom-dashboard/${goalRoomId}`);
  };

  return (
    <DialogBox defaultOpen>
      <DialogBackdrop asChild>
        <S.BackDrop>
          <DialogContent>
            <GoalRoomDetailDialogContent
              closeGoalroomDetail={closeGoalroomDetail}
              goalRoomId={goalRoomId}
            />

            <S.EnterGoalRoomButton onClick={enterGoalRoom}>
              골룸 대시보드 입장하기
            </S.EnterGoalRoomButton>
          </DialogContent>
        </S.BackDrop>
      </DialogBackdrop>
    </DialogBox>
  );
};

export default GoalRoomDetailDialog;
