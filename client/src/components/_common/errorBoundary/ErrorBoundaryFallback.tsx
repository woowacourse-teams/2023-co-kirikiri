import cryingElephant from '@assets/images/cryingelephant.png';
import * as S from './ErrorBoundaryFallback.styles';

type ErrorBoundaryFallbackProps = {
  errorMessage: string;
};

const ErrorBoundaryFallback = ({ errorMessage }: ErrorBoundaryFallbackProps) => {
  return (
    <S.ErrorBoundaryFallbackWrapper role='alert'>
      <S.FallbackContent>
        <S.CryingElephant src={cryingElephant} alt='crying elephant' />
        <S.FallbackErrorMessage>오류가 발생했어요</S.FallbackErrorMessage>
        <S.FallbackErrorMessage>{errorMessage}</S.FallbackErrorMessage>
      </S.FallbackContent>
    </S.ErrorBoundaryFallbackWrapper>
  );
};

export default ErrorBoundaryFallback;
