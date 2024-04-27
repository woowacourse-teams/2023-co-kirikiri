import * as S from './GoalRoomDashboardRoadmap.styles';
import { useFetchGoalRoom, useGoalRoomDetail } from '@/hooks/queries/goalRoom';
import { useGoalRoomDashboardContext } from '@/context/goalRoomDashboardContext';
import { Dialog } from 'ck-util-components';
import SVGIcon from '@components/icons/SVGIcon';
import RoadmapModal from '@components/goalRoomDahsboardPage/goalRoomDahsboardRoadmap/roadmapModal/RoadmapModal';

const GoalRoomDashboardRoadmap = () => {
  const { goalroomId } = useGoalRoomDashboardContext();
  const { goalRoom } = useFetchGoalRoom(goalroomId);
  const { goalRoomInfo } = useGoalRoomDetail(Number(goalroomId));

  return (
    <Dialog>
      <S.RoadmapWrapper>
        <div>
          <S.TitleWrapper>
            <h2>로드맵</h2>
          </S.TitleWrapper>

          <Dialog.Trigger asChild>
            <button>
              <span>전체보기</span>
              <SVGIcon name='RightArrowIcon' aria-hidden='true' />
            </button>
          </Dialog.Trigger>
        </div>
        <S.RoadmapContainer>
          {goalRoomInfo.goalRoomNodes.map((node) => {
            return (
              <S.NodeContainer>
                <S.NodePeriod>
                  {node.startDate} ~ {node.endDate}
                </S.NodePeriod>
                <S.NodeTitle>{node.title}</S.NodeTitle>
                <S.FeedCount>인증횟수 {node.checkCount}회</S.FeedCount>
              </S.NodeContainer>
            );
          })}
        </S.RoadmapContainer>
      </S.RoadmapWrapper>

      <Dialog.BackDrop asChild>
        <S.DashboardBackDrop />
      </Dialog.BackDrop>

      <Dialog.Content>
        <>{goalRoom.status === 'RUNNING' && <RoadmapModal goalroomId={goalroomId} />}</>
      </Dialog.Content>
    </Dialog>
  );
};

export default GoalRoomDashboardRoadmap;
