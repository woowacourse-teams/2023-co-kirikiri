import elephantImg from '@assets/images/elephant.png';
import elephantImgAV from '@assets/images/elephant.avif';
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
        <picture>
          <source srcSet={elephantImgAV} />
          <S.Elephant
            src={elephantImg}
            ref={elephantRef}
            style={{
              transform: `rotateX(${tilt.y}deg) rotateY(${tilt.x}deg) scaleX(${
                tilt.isLeftSide ? '-1' : '1'
              })`,
            }}
          />
        </picture>
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
