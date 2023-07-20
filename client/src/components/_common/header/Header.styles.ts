import styled from 'styled-components';
import media from '@styles/media';

export const HeaderWrapper = styled.div`
  position: fixed;
`;

export const Header = styled.div`
  position: relative;
  z-index: ${({ theme }) => theme.zIndex.header};

  align-items: center;

  width: 100vw;
  height: 8rem;

  background-color: ${({ theme }) => theme.colors.gray100});
  border-radius: 0;

  ${media.mobile`
    display: flex;
  `}

  display: none;
`;

export const Logo = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
`;

export const NavBarToggleIcon = styled.div`
  margin-left: 1.8rem;
  font-size: 5rem;
`;

export const NavBarOverlay = styled.div`
  position: absolute;
  z-index: ${({ theme }) => theme.zIndex.navBarOverlay};
  top: 0;
  right: 0;

  width: calc(100% - 60%);
  height: 100vh;

  background-color: rgba(0, 0, 0, 0.3);
`;
