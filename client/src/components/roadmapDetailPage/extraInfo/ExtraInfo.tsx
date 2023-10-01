import { RoadmapDetailType } from '@myTypes/roadmap/internal';
import * as S from './ExtraInfo.styles';
import SVGIcon from '@components/icons/SVGIcon';
import { CategoriesInfo } from '@constants/roadmap/category';

type ExtraInfoProps = {
  roadmapInfo: RoadmapDetailType;
};

const ExtraInfo = ({ roadmapInfo }: ExtraInfoProps) => {
  return (
    <S.ExtraInfo>
      <div>Created by {roadmapInfo.creator.name}</div>
      <S.RoadmapMetadata>
        <S.Category>
          카테고리: {roadmapInfo.category.name}
          <SVGIcon name={CategoriesInfo[roadmapInfo.category.id].iconName} />
        </S.Category>
        <S.Difficulty>난이도: {roadmapInfo.difficulty}</S.Difficulty>
        <S.RecommendedRoadmapPeriod>
          예상 소요시간: {roadmapInfo.recommendedRoadmapPeriod}일
        </S.RecommendedRoadmapPeriod>
      </S.RoadmapMetadata>
      <S.Tags>
        {roadmapInfo.tags.map((tag) => (
          <div key={tag.id}>#{tag.name}</div>
        ))}
      </S.Tags>
    </S.ExtraInfo>
  );
};

export default ExtraInfo;
