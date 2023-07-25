import * as S from './GoalRoomDashboardRoadmap.styles';
import SVGIcon from '@components/icons/SVGIcon';

const GoalRoomDashboardRoadmap = () => {
  return (
    <S.RoadmapWrapper>
      <div>
        <h2>로드맵</h2>
        <button>
          <span>전체보기</span>
          <SVGIcon name='RightArrowIcon' />
        </button>
      </div>
      <div>로드맵</div>
    </S.RoadmapWrapper>
  );
};

export default GoalRoomDashboardRoadmap;
