import {
  DialogBackdrop,
  DialogBox,
  DialogContent,
  DialogTrigger,
} from '@components/_common/dialog/dialog';
import * as S from './goalRoomDetailDialog.styles';
import GoalRoomDetailDialogContent from './GoalRoomDetailDialogContent';
import { Link } from 'react-router-dom';
import { useJoinGoalRoom } from '@/hooks/queries/goalRoom';

type GoalRoomDetailDialogProps = {
  goalRoomId: number;
  isJoined: boolean;
};

const GoalRoomDetailDialog = ({ goalRoomId, isJoined }: GoalRoomDetailDialogProps) => {
  const { joinGoalRoom } = useJoinGoalRoom({ goalRoomId: String(goalRoomId) });

  return (
    <DialogBox>
      <DialogTrigger asChild>
        <S.DetailButton>자세히 보기</S.DetailButton>
      </DialogTrigger>
      <DialogBackdrop asChild>
        <S.BackDrop>
          <DialogContent>
            <GoalRoomDetailDialogContent goalRoomId={goalRoomId} />
            {isJoined ? (
              <Link to={`/goalroom-dashboard/${goalRoomId}`}>
                <S.EnterGoalRoomButton>골룸 대시보드 입장하기</S.EnterGoalRoomButton>
              </Link>
            ) : (
              <S.EnterGoalRoomButton onClick={() => joinGoalRoom()}>
                골룸 참여하기
              </S.EnterGoalRoomButton>
            )}
          </DialogContent>
        </S.BackDrop>
      </DialogBackdrop>
    </DialogBox>
  );
};

export default GoalRoomDetailDialog;
