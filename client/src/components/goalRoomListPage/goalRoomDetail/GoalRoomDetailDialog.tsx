import {
  DialogBackdrop,
  DialogBox,
  DialogContent,
  DialogTrigger,
} from '@components/_common/dialog/dialog';
import * as S from './goalRoomDetailDialog.styles';
import GoalRoomDetailDialogContent from './GoalRoomDetailDialogContent';
import { useNavigate } from 'react-router-dom';

type GoalRoomDetailDialogProps = {
  goalRoomId: number;
  isJoined: boolean;
};

const GoalRoomDetailDialog = ({ goalRoomId, isJoined }: GoalRoomDetailDialogProps) => {
  console.log(isJoined);

  const navigate = useNavigate();

  const enterGoalRoom = () => {
    navigate(`/goalroom-dashboard/${goalRoomId}`);
  };

  return (
    <DialogBox>
      <DialogTrigger asChild>
        <S.DetailButton>자세히 보기</S.DetailButton>
      </DialogTrigger>
      <DialogBackdrop asChild>
        <S.BackDrop>
          <DialogContent>
            <GoalRoomDetailDialogContent goalRoomId={goalRoomId} />
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
