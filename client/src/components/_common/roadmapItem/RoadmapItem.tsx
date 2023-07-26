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
  const categoryIcon = <SVGIcon name={CategoriesInfo[item.category.id].iconName} />;
  const difficultyIcon = (
    <SVGIcon name={DIFFICULTY_ICON_NAME[item.difficulty]} size={50} />
  );

  return (
    <S.RoadmapItem hasBorder={hasBorder}>
      <S.ItemHeader>
        <S.AchieversCount>지금까지 1024명이 목표를 달성했어요!</S.AchieversCount>
        <S.ReviewersCount>❤️ 240</S.ReviewersCount>
      </S.ItemHeader>
      <div>
        <S.RoadmapTitle>{item.roadmapTitle}</S.RoadmapTitle>
        <S.Description>{item.introduction}</S.Description>
      </div>
      <S.ItemExtraInfos>
        <S.ExtraInfoBox>{categoryIcon}</S.ExtraInfoBox>
        <S.Difficulty>{difficultyIcon}</S.Difficulty>
        <S.RecommendedRoadmapPeriod>
          <div>{item.recommendedRoadmapPeriod}</div>
          <div>Days</div>
        </S.RecommendedRoadmapPeriod>
      </S.ItemExtraInfos>
      <Link to={`/roadmap/${item.roadmapId}`}>
        <Button>{hasBorder ? '자세히 보기' : '진행중인 골룸 보기'}</Button>
      </Link>
      <S.ItemFooter>
        <S.CreatedBy>Created by {item.creator.name}</S.CreatedBy>
        <S.Tags>
          <span># Programming</span>
          <span># Study</span>
        </S.Tags>
      </S.ItemFooter>
    </S.RoadmapItem>
  );
};

export default RoadmapItem;
