import { DialogTrigger } from '@/components/_common/dialog/dialog';
import * as S from './goalRoomDetailDialog.styles';

const GoalRoomDetailDialogContent = () => {
  return (
    <S.Container>
      <S.TitleWrapper>
        <div />
        <S.Title>골룸입니당</S.Title>
        <DialogTrigger asChild>
          <S.CloseButton>X</S.CloseButton>
        </DialogTrigger>
      </S.TitleWrapper>
      <S.Participant>
        <p>7</p>/10
      </S.Participant>
      <S.RoadmapContainer>
        <S.RoadmapTitle>🐘 로드맵 둘러보기🐘🐘</S.RoadmapTitle>
        <S.NodeContainer>
          <S.NodePeriod>2023-07-19 ~ 2023-07-30</S.NodePeriod>
          <S.NodeTitle>로드맵 1주차</S.NodeTitle>
          <S.FeedCount>인증횟수 17회</S.FeedCount>
        </S.NodeContainer>
      </S.RoadmapContainer>
    </S.Container>
  );
};

export default GoalRoomDetailDialogContent;
