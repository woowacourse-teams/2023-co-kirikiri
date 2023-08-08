import { useSwitch } from '@/hooks/_common/useSwitch';
import { GoalRoomDetailType } from '@/myTypes/goalRoom/internal';
import GoalRoomDetailDialog from '../goalRoomDetail/GoalRoomDetailDialog';
import * as S from './goalRoomList.styles';

const GoalRoomItem = ({ ...goalRoomInfo }: GoalRoomDetailType) => {
  const {
    isSwitchOn: showDetail,
    turnSwitchOn: showGoalroomDetail,
    turnSwitchOff: closeGoalroomDetail,
  } = useSwitch(false);

  return (
    <>
      <S.ItemContainer>
        <S.Recruiting>모집중 (~{goalRoomInfo.startDate})</S.Recruiting>
        <S.Name>{goalRoomInfo.name}</S.Name>
        <S.RoadmapCreator>created by Woody ❤️</S.RoadmapCreator>
        <S.RoadmapIntroduce>please save me</S.RoadmapIntroduce>
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
        <S.DetailButton onClick={showGoalroomDetail}>자세히 보기</S.DetailButton>
      </S.ItemContainer>
      {showDetail && (
        <GoalRoomDetailDialog
          closeGoalroomDetail={closeGoalroomDetail}
          goalRoomId={goalRoomInfo.goalRoomId}
          isJoined={goalRoomInfo.isJoined}
        />
      )}
    </>
  );
};

export default GoalRoomItem;
