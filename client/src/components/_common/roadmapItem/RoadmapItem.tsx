import { CategoriesInfo } from '@constants/roadmap/Category';
import SVGIcon from '@components/icons/SVGIcon';
import { RoadmapItemType } from '@myTypes/roadmap';
import { Link } from 'react-router-dom';
import { DIFFICULTY_ICON_NAME } from '@constants/roadmap/Difficulty';
import Box from '../box/Box';
import * as S from './RoadmapItem.styles';

type RoadmapItemProps = {
  item: RoadmapItemType;
} & {
  dummyCategoryId?: keyof typeof CategoriesInfo;
};

const RoadmapItem = ({ item, dummyCategoryId }: RoadmapItemProps) => {
  const categoryIcon = (
    <SVGIcon name={CategoriesInfo[dummyCategoryId || item.category.id].iconName} />
  );
  const difficultyIcon = (
    <SVGIcon name={DIFFICULTY_ICON_NAME[item.difficulty]} size={50} />
  );

  return (
    <S.RoadmapItem>
      <S.ItemHeader>
        <S.AchieversCount>지금까지 1024명이 목표를 달성했어요!</S.AchieversCount>
        <S.ReviewersCount>❤️ 240</S.ReviewersCount>
      </S.ItemHeader>
      <S.ItemInfos>
        <S.RoadmapTitle>{item.roadmapTitle}</S.RoadmapTitle>
        <S.Description>{item.introduction}</S.Description>
      </S.ItemInfos>
      <S.ItemExtraInfos>
        <Box>{categoryIcon}</Box>
        <Box>
          <div style={{ marginTop: '2.5rem' }}>{difficultyIcon}</div>
        </Box>
        <Box>
          <div style={{ fontWeight: '700', fontSize: '2rem' }}>
            {item.recommendedRoadmapPeriod}
          </div>
          <div>Days</div>
        </Box>
      </S.ItemExtraInfos>
      <Link to={`/roadmap/${item.roadmapId}`}>
        <S.SeeDetailButton>자세히 보기</S.SeeDetailButton>
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
