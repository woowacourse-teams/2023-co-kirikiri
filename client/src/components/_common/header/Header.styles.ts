import styled from 'styled-components';
import media from '@styles/media';

export const HeaderWrapper = styled.div`
  position: fixed;
`;

export const Header = styled.div`
  position: relative;
  z-index: 100;

  display: none;
  align-items: center;

  width: 100vw;
  height: 56px;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 0;

  ${media.mobile`
    display: flex;
  `}
`;

export const Logo = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
`;

export const NavBarToggleIcon = styled.div`
  margin-left: 18px;
  font-size: 5rem;
`;

export const CloseNavBackground = styled.div`
  position: absolute;
  z-index: 200;
  top: 0;

  width: calc(100% - 15rem);
  height: 100vh;
  margin-left: 15rem;

  background-color: rgba(1, 1, 1, 0.3);
`;
