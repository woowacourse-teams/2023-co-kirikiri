import {
  DialogBackdrop,
  DialogBox,
  DialogContent,
  DialogTrigger,
} from '@components/_common/dialog/dialog';
import * as S from './goalRoomDetailDialog.styles';
import GoalRoomDetailDialogContent from './GoalRoomDetailDialogContent';
import { Link } from 'react-router-dom';

type GoalRoomDetailDialogProps = {
  goalRoomId: number;
  isJoined: boolean;
};

const GoalRoomDetailDialog = ({ goalRoomId, isJoined }: GoalRoomDetailDialogProps) => {
  console.log(isJoined);

  return (
    <DialogBox>
      <DialogTrigger asChild>
        <S.DetailButton>자세히 보기</S.DetailButton>
      </DialogTrigger>
      <DialogBackdrop asChild>
        <S.BackDrop>
          <DialogContent>
            <GoalRoomDetailDialogContent goalRoomId={goalRoomId} />
            <Link to={`/goalroom-dashboard/${goalRoomId}`}>
              <S.EnterGoalRoomButton>골룸 대시보드 입장하기</S.EnterGoalRoomButton>
            </Link>
          </DialogContent>
        </S.BackDrop>
      </DialogBackdrop>
    </DialogBox>
  );
};

export default GoalRoomDetailDialog;
