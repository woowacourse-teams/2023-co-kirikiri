import * as S from './goalRoomList.styles';

const GoalRoomItem = ({ ...goalRoomInfo }: any) => {
  return (
    <S.ItemContainer>
      <S.Recruiting>{goalRoomInfo.startDate}모집중</S.Recruiting>
      <S.Name>{goalRoomInfo.name}dkdkdkdkdkdk</S.Name>
      <S.RoadmapCreator>created by Woody ❤️</S.RoadmapCreator>
      <S.RoadmapIntroduce>please save me</S.RoadmapIntroduce>
      <S.Period>
        진행기간 <p>{goalRoomInfo.startDate - goalRoomInfo.endDate} 7월 12일</p>
      </S.Period>
      <S.Particpant>
        진행기간 <p>{goalRoomInfo.startDate - goalRoomInfo.endDate} 7월 12일</p>
      </S.Particpant>
      <S.DetailButton>자세히 보기</S.DetailButton>
    </S.ItemContainer>
  );
};

export default GoalRoomItem;
