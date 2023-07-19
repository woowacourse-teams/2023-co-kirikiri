import SVGIcon from '@components/icons/SVGIcon';
import { MouseEventHandler } from 'react';
import * as S from './BackButton.styles';

type BackButtonProps = {
  action: MouseEventHandler<HTMLButtonElement>;
};

const BackButton = ({ action }: BackButtonProps) => {
  return (
    <S.BackButtonContainer onClick={action}>
      <SVGIcon name='BackArrowIcon' />
    </S.BackButtonContainer>
  );
};

export default BackButton;
