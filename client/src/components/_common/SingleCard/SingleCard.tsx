import { PropsWithChildren } from 'react';
import * as S from './SingleCard.styles';

const SingleCard = ({ children }: PropsWithChildren) => {
  return <S.SingleCardWrapper>{children}</S.SingleCardWrapper>;
};

export default SingleCard;
