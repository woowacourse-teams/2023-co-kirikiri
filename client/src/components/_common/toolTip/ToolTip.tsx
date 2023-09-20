import { useSwitch } from '@/hooks/_common/useSwitch';
import React, { PropsWithChildren } from 'react';
import * as S from './ToolTip.styles';

const ToolTip = ({ children }: PropsWithChildren) => {
  const {
    isSwitchOn: isHover,
    turnSwitchOn: setHoverOn,
    turnSwitchOff: setHoverOff,
  } = useSwitch(false);
  const { isSwitchOn: isActive, toggleSwitch: toggleActive } = useSwitch(false);

  const isShow = isHover || isActive;

  const onClickToolTipButton = (e: React.MouseEvent<HTMLButtonElement>) => {
    toggleActive();
    e.stopPropagation();
  };

  return (
    <S.ToolTip>
      <S.ToolTipButton
        isActive={isActive}
        onMouseEnter={setHoverOn}
        onMouseLeave={setHoverOff}
        onClick={onClickToolTipButton}
      >
        ?
      </S.ToolTipButton>
      <S.ToolTipContent isShow={isShow}>{children}</S.ToolTipContent>
    </S.ToolTip>
  );
};

export default ToolTip;
