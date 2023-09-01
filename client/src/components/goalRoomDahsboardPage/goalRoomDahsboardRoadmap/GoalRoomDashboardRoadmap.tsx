import * as S from './GoalRoomDashboardRoadmap.styles';
// import SVGIcon from '@components/icons/SVGIcon';
import { useGoalRoomDetail } from '@/hooks/queries/goalRoom';
import { useGoalRoomDashboardContext } from '@/context/goalRoomDashboardContext';

const GoalRoomDashboardRoadmap = () => {
  const { goalroomId } = useGoalRoomDashboardContext();
  const { goalRoomInfo } = useGoalRoomDetail(Number(goalroomId));

  return (
    <S.RoadmapWrapper>
      <div>
        <h2>로드맵</h2>
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
  );
};

export default GoalRoomDashboardRoadmap;
