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
    <SVGIcon name={DIFFICULTY_ICON_NAME[item.difficulty]} size={50} />
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
