import * as S from './listTitle.styles';

const DummyRoadmapData = {
  roadmapTitle: '50일 자바스크립트 완전 정복',
  creator: 'Woody',
  introduce: '한줄 설명으로 사용자들의 시선을 사로 잡아야만 한다는 것이다',
};

const ListTitle = () => {
  return (
    <S.Container>
      <S.RaodmapTitle>{DummyRoadmapData.roadmapTitle}</S.RaodmapTitle>
      <S.RoadmapCreator>Created by {DummyRoadmapData.creator} ❤️</S.RoadmapCreator>
      <S.RoadmapIntroduce>{DummyRoadmapData.introduce}</S.RoadmapIntroduce>
    </S.Container>
  );
};

export default ListTitle;
