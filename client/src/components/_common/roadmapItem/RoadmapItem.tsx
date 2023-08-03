import type { RoadmapItemType } from '@myTypes/roadmap/internal';

import { Link } from 'react-router-dom';
import SVGIcon from '@components/icons/SVGIcon';
import { DIFFICULTY_ICON_NAME } from '@constants/roadmap/difficulty';
import { CategoriesInfo } from '@constants/roadmap/category';
import Button from '../button/Button';
import * as S from './RoadmapItem.styles';

type RoadmapItemProps = {
  item: RoadmapItemType;
  hasBorder?: boolean;
};

const RoadmapItem = ({ item, hasBorder = true }: RoadmapItemProps) => {
  const categoryIcon = (
    <div aria-label={CategoriesInfo[item.category.id].name}>
      <SVGIcon name={CategoriesInfo[item.category.id].iconName} aria-hidden='true' />
    </div>
  );
  const difficultyIcon = (
    <div aria-label={DIFFICULTY_ICON_NAME[item.difficulty]}>
      <SVGIcon
        name={DIFFICULTY_ICON_NAME[item.difficulty]}
        size={50}
        aria-hidden='true'
      />
    </div>
  );

  return (
    <S.RoadmapItem hasBorder={hasBorder} aria-label='로드맵 항목'>
      <S.ItemHeader>
        <S.AchieversCount aria-label='목표 달성률'>
          지금까지 1024명이 목표를 달성했어요!
        </S.AchieversCount>
        <S.ReviewersCount aria-label='로드맵 좋아요 개수'>❤️ 240</S.ReviewersCount>
      </S.ItemHeader>
      <div>
        <S.RoadmapTitle aria-label='로드맵 제목'>{item.roadmapTitle}</S.RoadmapTitle>
        <S.Description aria-label='로드맵 소개'>{item.introduction}</S.Description>
      </div>
      <S.ItemExtraInfos aria-label='로드맵 속성'>
        <S.ExtraInfoBox aria-label='로드맵 카테고리 분류'>{categoryIcon}</S.ExtraInfoBox>
        <S.Difficulty aria-label='로드맵 난이도'>{difficultyIcon}</S.Difficulty>
        <S.RecommendedRoadmapPeriod aria-label='로드맵 진행 기간'>
          <div>{item.recommendedRoadmapPeriod}</div>
          <div aria-label='일'>Days</div>
        </S.RecommendedRoadmapPeriod>
      </S.ItemExtraInfos>
      <Link to={`/roadmap/${item.roadmapId}`}>
        <Button aria-label={hasBorder ? '자세히 보기' : '진행중인 골룸 보기'}>
          {hasBorder ? '자세히 보기' : '진행중인 골룸 보기'}
        </Button>
      </Link>
      <S.ItemFooter aria-label='로드맵 추가 정보'>
        <S.CreatedBy aria-label='로드맵 생성자'>
          Created by {item.creator.name}
        </S.CreatedBy>
        <S.Tags aria-label='로드맵 태그 목록'>
          <span># Programming</span>
          <span># Study</span>
        </S.Tags>
      </S.ItemFooter>
    </S.RoadmapItem>
  );
};

export default RoadmapItem;
