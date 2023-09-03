import { useState, PropsWithChildren } from 'react';
import * as S from './ToolTip.styles';

const ToolTip = ({ children }: PropsWithChildren) => {
  const [isHover, setIsHover] = useState(false);
  const [isActive, setIsActive] = useState(false);

  const isShow = isHover || isActive;

  return (
    <S.ToolTip>
      <S.ToolTipButton
        isActive={isActive}
        onMouseEnter={() => setIsHover(true)}
        onMouseLeave={() => setIsHover(false)}
        onClick={() => setIsActive((prev) => !prev)}
      >
        !
      </S.ToolTipButton>
      <S.ToolTipContent isShow={isShow}>{children}</S.ToolTipContent>
    </S.ToolTip>
  );
};

export default ToolTip;
