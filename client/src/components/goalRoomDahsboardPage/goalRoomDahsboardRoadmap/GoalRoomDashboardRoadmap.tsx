import * as S from './GoalRoomDashboardRoadmap.styles';
import { useGoalRoomDetail, useGoalRoomNodeList } from '@/hooks/queries/goalRoom';
import { useGoalRoomDashboardContext } from '@/context/goalRoomDashboardContext';
import {
  DialogBackdrop,
  DialogBox,
  DialogContent,
  DialogTrigger,
} from '@components/_common/dialog/dialog';
import SVGIcon from '@components/icons/SVGIcon';
import RoadmapModal from '@components/goalRoomDahsboardPage/goalRoomDahsboardRoadmap/roadmapModal/RoadmapModal';

const GoalRoomDashboardRoadmap = () => {
  const { goalroomId } = useGoalRoomDashboardContext();
  const { goalRoomInfo } = useGoalRoomDetail(Number(goalroomId));
  const { goalRoomNodeList } = useGoalRoomNodeList(goalroomId);

  return (
    <DialogBox>
      <S.RoadmapWrapper>
        <div>
          <S.TitleWrapper>
            <h2>로드맵</h2>
          </S.TitleWrapper>

          <DialogTrigger asChild>
            <button>
              <span>전체보기</span>
              <SVGIcon name='RightArrowIcon' aria-hidden='true' />
            </button>
          </DialogTrigger>
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

      <DialogBackdrop asChild>
        <S.DashboardBackDrop />
      </DialogBackdrop>

      <DialogContent>
        <RoadmapModal nodeList={goalRoomNodeList} />
      </DialogContent>
    </DialogBox>
  );
};

export default GoalRoomDashboardRoadmap;
