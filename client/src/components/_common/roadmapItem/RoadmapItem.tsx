import type { RoadmapDetailType } from '@myTypes/roadmap/internal';

import { useNavigate } from 'react-router-dom';
import SVGIcon from '@components/icons/SVGIcon';
import { DIFFICULTY_ICON_NAME } from '@constants/roadmap/difficulty';
import { CategoriesInfo } from '@constants/roadmap/category';
import Button from '../button/Button';
import * as S from './RoadmapItem.styles';

type RoadmapItemProps = {
  item: Omit<RoadmapDetailType, 'content'>;
  hasBorder?: boolean;
  roadmapId: number;
};

const RoadmapItem = ({ item, hasBorder = true, roadmapId }: RoadmapItemProps) => {
  const categoryIcon = <SVGIcon name={CategoriesInfo[item.category.id].iconName} />;
  const difficultyIcon = (
    <div aria-label={DIFFICULTY_ICON_NAME[item.difficulty]}>
      <SVGIcon
        name={DIFFICULTY_ICON_NAME[item.difficulty]}
        size={50}
        aria-hidden='true'
      />
    </div>
  );
  const navigate = useNavigate();

  const moveGoalRoomListPage = () => {
    if (hasBorder) {
      navigate(`/roadmap/${roadmapId}`);
      return;
    }
    navigate(`/roadmap/${roadmapId}/goalroom-list`);
  };

  return (
    <S.RoadmapItem hasBorder={hasBorder} aria-label='로드맵 항목'>
      <div>
        <S.RoadmapTitle aria-label='로드맵 제목'>{item.roadmapTitle}</S.RoadmapTitle>
        <S.Description aria-label='로드맵 소개'>{item.introduction}</S.Description>
      </div>
      <S.ExtraHeader>
        <S.ExtraHeaderText>카테고리</S.ExtraHeaderText>
        <S.ExtraHeaderText>난이도</S.ExtraHeaderText>
        <S.ExtraHeaderText>권장 소요기간</S.ExtraHeaderText>
      </S.ExtraHeader>
      <S.ItemExtraInfos aria-label='로드맵 속성'>
        <S.ExtraInfoBox aria-label='로드맵 카테고리 분류'>{categoryIcon}</S.ExtraInfoBox>
        <S.Difficulty aria-label='로드맵 난이도'>{difficultyIcon}</S.Difficulty>
        <S.RecommendedRoadmapPeriod aria-label='로드맵 진행 기간'>
          <S.RecommendedRoadmapPeriodNumber>
            {item.recommendedRoadmapPeriod}
          </S.RecommendedRoadmapPeriodNumber>
          일
        </S.RecommendedRoadmapPeriod>
      </S.ItemExtraInfos>
      <Button onClick={moveGoalRoomListPage}>
        {hasBorder ? '자세히 보기' : '진행중인 골룸 보기'}
      </Button>
      <S.ItemFooter>
        <S.CreatedBy>Created by {item.creator.name}</S.CreatedBy>
        <S.Tags>
          {item.tags.map((tag) => {
            return <span># {tag.name}</span>;
          })}
        </S.Tags>
      </S.ItemFooter>
    </S.RoadmapItem>
  );
};

export default RoadmapItem;
