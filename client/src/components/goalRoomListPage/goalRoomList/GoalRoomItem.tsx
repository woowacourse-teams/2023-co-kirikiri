import { GoalRoomDetailType } from '@/myTypes/goalRoom/internal';
import * as S from './goalRoomList.styles';

const GoalRoomItem = ({ ...goalRoomInfo }: GoalRoomDetailType) => {
  return (
    <S.ItemContainer>
      <S.Recruiting>{goalRoomInfo.startDate}</S.Recruiting>
      <S.Name>{goalRoomInfo.name}</S.Name>
      <S.RoadmapCreator>created by Woody ❤️</S.RoadmapCreator>
      <S.RoadmapIntroduce>please save me</S.RoadmapIntroduce>
      <S.Period>
        진행기간{' '}
        <p>
          {goalRoomInfo.startDate} - {goalRoomInfo.endDate}
        </p>
      </S.Period>
      <S.Particpant>
        진행기간{' '}
        <p>
          {goalRoomInfo.startDate} - {goalRoomInfo.endDate}
        </p>
      </S.Particpant>
      <S.DetailButton>자세히 보기</S.DetailButton>
    </S.ItemContainer>
  );
};

export default GoalRoomItem;
