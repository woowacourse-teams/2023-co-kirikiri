import * as S from './errorComponents.styles';

export const NotFound = () => {
  return <S.Container>404 not found</S.Container>;
};

export const ServerError = () => {
  return <S.Container>505 server error</S.Container>;
};

export const Runtime = () => {
  return <S.Container>runtime error</S.Container>;
};

export const Critical = () => {
  return <S.Container>global error</S.Container>;
};
