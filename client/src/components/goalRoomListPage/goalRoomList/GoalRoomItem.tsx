import { GoalRoomDetailType } from '@/myTypes/goalRoom/internal';
import GoalRoomDetailDialog from '../goalRoomDetail/GoalRoomDetailDialog';
import * as S from './goalRoomList.styles';

const GoalRoomItem = ({ ...goalRoomInfo }: GoalRoomDetailType) => {
  return (
    <S.ItemContainer>
      <S.Recruiting>모집중 (~{goalRoomInfo.startDate})</S.Recruiting>
      <S.Name>{goalRoomInfo.name}</S.Name>
      <S.RoadmapCreator>created by {goalRoomInfo.goalRoomLeader.name}</S.RoadmapCreator>
      <S.Period>
        진행기간
        <p>
          {goalRoomInfo.startDate} - {goalRoomInfo.endDate}
        </p>
      </S.Period>
      <S.Particpant>
        참여인원
        <p>
          {goalRoomInfo.currentMemberCount} / {goalRoomInfo.limitedMemberCount}
        </p>
      </S.Particpant>
      <GoalRoomDetailDialog
        goalRoomId={goalRoomInfo.goalRoomId}
        isJoined={goalRoomInfo.isJoined}
      />
    </S.ItemContainer>
  );
};

export default GoalRoomItem;
