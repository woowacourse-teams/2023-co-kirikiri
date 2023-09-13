import * as S from './errorComponents.styles';
import elephantImage from '../../../assets/images/cryingelephant.png';
import { useNavigate } from 'react-router-dom';

export const NotFound = () => {
  const navigate = useNavigate();

  const moveMainPage = () => {
    navigate('/');
    window.location.reload();
  };
  return (
    <S.Container>
      <S.ElephantImage src={elephantImage} alt='crying-elephant' />
      <S.NotFoundTitle>404 Not Found</S.NotFoundTitle>
      <S.NotFoundText>잘못된 경로 접근했어요</S.NotFoundText>
      <S.MovePageButton onClick={moveMainPage}>메인페이지로 돌아가기</S.MovePageButton>
    </S.Container>
  );
};

export const ServerError = () => {
  const navigate = useNavigate();

  const moveMainPage = () => {
    navigate('/');
    window.location.reload();
  };
  return (
    <S.Container>
      <S.ElephantImage src={elephantImage} alt='crying-elephant' />
      <S.SereverTitle>500 Error</S.SereverTitle>
      <S.ServerText>서버에서 오류가 발생했어요</S.ServerText>
      <S.MovePageButton onClick={moveMainPage}>메인페이지로 돌아가기</S.MovePageButton>
    </S.Container>
  );
};

export const Runtime = () => {
  const navigate = useNavigate();

  const moveMainPage = () => {
    navigate('/');
    window.location.reload();
  };
  return (
    <S.Container>
      <S.ElephantImage src={elephantImage} alt='crying-elephant' />
      <S.RuntimeTitle>Error</S.RuntimeTitle>
      <S.RuntimeText>
        죄송합니다, 페이지를 로드하는 동안 오류가 발생했습니다
      </S.RuntimeText>
      <S.MovePageButton onClick={moveMainPage}>메인페이지로 돌아가기</S.MovePageButton>
    </S.Container>
  );
};

export const Critical = () => {
  const navigate = useNavigate();

  const moveMainPage = () => {
    navigate('/');
    window.location.reload();
  };
  return (
    <S.Container>
      <S.ElephantImage src={elephantImage} alt='crying-elephant' />
      <S.CriticalTitle>Service Not Working</S.CriticalTitle>
      <S.CriticalText>
        죄송합니다, 알 수 없는 이유로 서비스 사용이 불가능합니다
      </S.CriticalText>
      <S.MovePageButton onClick={moveMainPage}>메인페이지로 돌아가기</S.MovePageButton>
    </S.Container>
  );
};
