import * as S from './fallback.styles';
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

export const Forbidden = () => {
  const navigate = useNavigate();

  const moveMainPage = () => {
    navigate('/');
    window.location.reload();
  };
  return (
    <S.Container>
      <S.ElephantImage src={elephantImage} alt='crying-elephant' />
      <S.ForbiddenTitle>403 Forbidden</S.ForbiddenTitle>
      <S.ForbiddenText>접근할 수 없는 페이지입니다</S.ForbiddenText>
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
  return (
    <S.Container>
      <S.ElephantImage src={elephantImage} alt='crying-elephant' />
      <S.CriticalTitle>Service Not Working</S.CriticalTitle>
      <S.CriticalText>
        죄송합니다, 알 수 없는 이유로 서비스 사용이 불가능합니다 😭 <br />
        다음에 다시 이용해주세요.
      </S.CriticalText>
      <S.MovePageButton>불편사항 문의하기</S.MovePageButton>
    </S.Container>
  );
};

export const Offline = () => {
  const navigate = useNavigate();

  const movePrevPage = () => {
    navigate(-1);
  };
  return (
    <S.Container>
      <S.CriticalText>네트워크 상태가 불안정합니다.</S.CriticalText>
      <S.MovePageButton onClick={() => window.location.reload()}>
        다시 시도하기
      </S.MovePageButton>
      <S.MovePageButton onClick={movePrevPage}>이전 페이지로 돌아가기</S.MovePageButton>
    </S.Container>
  );
};
