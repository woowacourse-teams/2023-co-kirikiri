import elephantImg from '@assets/images/elephant.png';
import * as S from './MainPage.styles';
import { useNavigate } from 'react-router-dom';

const MainPage = () => {
  const navigate = useNavigate();

  const listButtonHandler = () => {
    navigate('/roadmap-list');
  };

  return (
    <S.MainPageWrapper>
      <S.MainPageContent>
        <S.Elephant src={elephantImg} />
        <S.MainPageDesc>
          우리들이 함께 만들어가는 로드맵 기반 스터디 플랫폼.
        </S.MainPageDesc>
        <S.MainPageTitle>코끼리끼리</S.MainPageTitle>
        <S.GoalRoomListButton onClick={listButtonHandler}>
          지금 바로 로드맵 확인하러 가기
        </S.GoalRoomListButton>
      </S.MainPageContent>
    </S.MainPageWrapper>
  );
};

export default MainPage;
