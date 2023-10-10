import { RoadmapDetailType } from '@myTypes/roadmap/internal';
import * as S from './ExtraInfo.styles';

type ExtraInfoProps = {
  roadmapInfo: RoadmapDetailType;
};

const ExtraInfo = ({ roadmapInfo }: ExtraInfoProps) => {
  return (
    <S.ExtraInfo>
      <p>로드맵 정보</p>
      <S.RoadmapMetadata>
        <S.Category>
          카테고리: <p>{roadmapInfo.category.name}</p>
        </S.Category>
        <S.Difficulty>
          난이도: <p>{roadmapInfo.difficulty}</p>
        </S.Difficulty>
        <S.RecommendedRoadmapPeriod>
          예상 소요시간: <p>{roadmapInfo.recommendedRoadmapPeriod}일</p>
        </S.RecommendedRoadmapPeriod>
      </S.RoadmapMetadata>
    </S.ExtraInfo>
  );
};

export default ExtraInfo;
