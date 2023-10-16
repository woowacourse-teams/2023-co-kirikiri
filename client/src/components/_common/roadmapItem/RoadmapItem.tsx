import type { RoadmapDetailType } from '@myTypes/roadmap/internal';

import { useNavigate } from 'react-router-dom';
import SVGIcon from '@components/icons/SVGIcon';
import { DIFFICULTY_ICON_NAME } from '@constants/roadmap/difficulty';
import { CategoriesInfo } from '@constants/roadmap/category';
import Button from '../button/Button';
import * as S from './RoadmapItem.styles';
import useHover from '@/hooks/_common/useHover';

type RoadmapItemProps = {
  item: Omit<RoadmapDetailType, 'content'>;
  hasBorder?: boolean;
  roadmapId: number;
};

const RoadmapItem = ({ item, hasBorder = true, roadmapId }: RoadmapItemProps) => {
  const categoryIcon = <SVGIcon name={CategoriesInfo[item.category.id].iconName} />;
  const difficultyIcon = (
    <div>
      <SVGIcon
        name={DIFFICULTY_ICON_NAME[item.difficulty]}
        size={50}
        aria-label={DIFFICULTY_ICON_NAME[item.difficulty]}
      />
    </div>
  );
  const navigate = useNavigate();
  const { isHovered, handleMouseEnter, handleMouseLeave } = useHover();

  const moveGoalRoomListPage = () => {
    if (hasBorder) {
      navigate(`/roadmap/${roadmapId}`);
      return;
    }
    navigate(`/roadmap/${roadmapId}/goalroom-list`);
  };

  return (
    <S.RoadmapItem $hasBorder={hasBorder} aria-label='로드맵 항목'>
      <div>
        <S.RoadmapTitle aria-label='로드맵 제목'>{item.roadmapTitle}</S.RoadmapTitle>
        <S.Description
          aria-label='로드맵 소개'
          onMouseEnter={handleMouseEnter}
          onMouseLeave={handleMouseLeave}
        >
          {item.introduction}
        </S.Description>
        {isHovered && <S.HoverDescription>{item.introduction}</S.HoverDescription>}
      </div>
      <S.ExtraHeader>
        <S.ExtraHeaderText>카테고리</S.ExtraHeaderText>
        <S.ExtraHeaderText>난이도</S.ExtraHeaderText>
        <S.ExtraHeaderText>권장 소요기간</S.ExtraHeaderText>
      </S.ExtraHeader>
      <S.ItemExtraInfos aria-label='로드맵 속성'>
        <S.ExtraInfoBox>{categoryIcon}</S.ExtraInfoBox>
        <S.Difficulty>{difficultyIcon}</S.Difficulty>
        <S.RecommendedRoadmapPeriod>
          <S.RecommendedRoadmapPeriodNumber>
            {item.recommendedRoadmapPeriod}
          </S.RecommendedRoadmapPeriodNumber>
          일
        </S.RecommendedRoadmapPeriod>
      </S.ItemExtraInfos>
      <Button onClick={moveGoalRoomListPage}>
        {hasBorder ? '자세히 보기' : '진행중인 모임 보기'}
      </Button>
      <S.ItemFooter>
        <S.Tags>
          {item.tags.map((tag) => {
            return <span key={tag.id}># {tag.name}</span>;
          })}
        </S.Tags>
        <S.CreatedBy>Created by {item.creator.name}</S.CreatedBy>
      </S.ItemFooter>
    </S.RoadmapItem>
  );
};

export default RoadmapItem;
