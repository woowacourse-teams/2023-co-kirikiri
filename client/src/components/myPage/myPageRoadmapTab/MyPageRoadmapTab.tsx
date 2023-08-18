import * as S from './MyPageRoadmapTap.styles';
import { useMyRoadmapList } from '@hooks/queries/roadmap';
import { DIFFICULTY_TEXT } from '@constants/roadmap/difficulty';
import { useUserInfoContext } from '@components/_providers/UserInfoProvider';
import SVGIcon from '@components/icons/SVGIcon';
import formatDate from '@utils/_common/formatDate';

const MyPageRoadmapTab = () => {
  const { myRoadmapList } = useMyRoadmapList();
  const { userInfo } = useUserInfoContext();

  return (
    <>
      <S.RoadmapTabTitle>
        <span>나의 로드맵</span>
        <SVGIcon name='RoadmapIcon' />
      </S.RoadmapTabTitle>
      <S.RoadmapTabView>
        <S.RoadmapTabContent>
          <S.RoadmapHeader>
            <div>이름</div>
            <div>난이도</div>
            <div>생성일자</div>
            <div>생성자</div>
            <div>카테고리</div>
          </S.RoadmapHeader>

          {myRoadmapList.responses.map((roadmap, index) => (
            <S.RoadmapDetails key={roadmap.roadmapId} isOddRow={index % 2 === 1}>
              <div>
                <S.RoadmapLink to={`/roadmap/${roadmap.roadmapId}`}>
                  {roadmap.roadmapTitle}
                </S.RoadmapLink>
              </div>
              <div>
                <S.RoadmapStatus>{DIFFICULTY_TEXT[roadmap.difficulty]}</S.RoadmapStatus>
              </div>
              <div>{formatDate(roadmap.createdAt)}</div>
              <div>{userInfo.nickname}</div>
              <div>{roadmap.category.name}</div>
            </S.RoadmapDetails>
          ))}
        </S.RoadmapTabContent>
      </S.RoadmapTabView>
    </>
  );
};

export default MyPageRoadmapTab;
