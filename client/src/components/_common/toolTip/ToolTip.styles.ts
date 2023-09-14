import styled from 'styled-components';

export const ToolTip = styled.div`
  position: relative;
  display: inline-block;
  width: 18px;
`;

export const ToolTipButton = styled.button<{ isActive: boolean }>`
  cursor: pointer;

  display: flex;
  align-items: center;
  justify-content: center;

  width: 18px;
  height: 18px;

  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme, isActive }) =>
    isActive ? theme.colors.main_dark : theme.colors.gray300};
  border-radius: 50%;
`;

export const ToolTipContent = styled.p<{ isShow: boolean }>`
  ${({ theme }) => theme.fonts.description4}
  position: absolute;
  top: 150%;
  left: 50%;

  width: 16rem;
  margin-left: -8rem;
  padding: 0.5rem;

  color: ${({ theme }) => theme.colors.black};
  text-align: center;

  visibility: ${({ isShow }) => (isShow ? 'visible' : 'hidden')};
  background-color: ${({ theme }) => theme.colors.gray200};
  border-radius: 6px;

  & > strong {
    color: ${({ theme }) => theme.colors.main_dark};
  }

  &::after {
    content: '';

    position: absolute;
    bottom: 100%;
    left: 50%;

    margin-left: -0.5rem;

    border-color: ${({ theme }) =>
      `transparent transparent ${theme.colors.gray200} transparent`};
    border-style: solid;
    border-width: 0.5rem;
  }
`;
