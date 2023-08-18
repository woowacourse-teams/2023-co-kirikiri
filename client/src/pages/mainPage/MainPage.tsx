import elephantImg from '@assets/images/elephant.png';
import * as S from './MainPage.styles';
import { useNavigate } from 'react-router-dom';
import { useRef, useState } from 'react';

const MainPage = () => {
  const navigate = useNavigate();
  const [tilt, setTilt] = useState({ x: 0, y: 0, isLeftSide: false });
  const elephantRef = useRef<HTMLImageElement>(null);

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    if (elephantRef.current) {
      const { left, top, width, height } = elephantRef.current.getBoundingClientRect();
      const x = (e.clientX - (left + width / 2)) / 10;
      const y = (e.clientY - (top + height / 2)) / 10;

      const isLeftSide = e.clientX < left + width / 2;

      setTilt({ x, y, isLeftSide });
    }
  };
  const listButtonHandler = () => {
    navigate('/roadmap-list');
  };

  return (
    <S.MainPageWrapper onMouseMove={handleMouseMove}>
      <S.MainPageContent>
        <S.Elephant
          src={elephantImg}
          ref={elephantRef}
          style={{
            transform: `rotateX(${tilt.y}deg) rotateY(${tilt.x}deg) scaleX(${
              tilt.isLeftSide ? '-1' : '1'
            })`,
          }}
        />
        <S.MainPageDesc>
          우리들이 함께 만들어가는 로드맵 기반 스터디 플랫폼.
        </S.MainPageDesc>
        <S.MainPageTitle>코끼리끼리</S.MainPageTitle>
        <S.ServiceDescWrapper>
          <S.ServiceDescContent>
            <S.ServiceDesc>
              코끼리끼리에서 나만의 로드맵을 자유롭게 제공해보세요! <br /> 전문적인 지식
              공유도 좋고, 가벼운 취미 공유도 좋습니다. <br /> 매력적인 로드맵은
              여러사람들을 움직이게 할 거 에요! <br /> 로드맵을 달성하길 원하는 사람들은
              '목표를 달성하는 방'이라는 뜻의 골룸을 생성해서 모일 수 있어요. <br />{' '}
              코끼리끼리를 통해 함께 목표를 달성해봐요!
            </S.ServiceDesc>
          </S.ServiceDescContent>
        </S.ServiceDescWrapper>
        <S.GoalRoomListButton onClick={listButtonHandler}>
          지금 바로 로드맵 확인하러 가기
        </S.GoalRoomListButton>
      </S.MainPageContent>
    </S.MainPageWrapper>
  );
};

export default MainPage;
