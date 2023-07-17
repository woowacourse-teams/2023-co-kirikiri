import { styled } from 'styled-components';
import media from '@styles/media';

export const NavBar = styled.nav<{ isNavBarOpen: boolean }>`
  position: absolute;
  z-index: 10;
  top: 0;

  width: 15rem;
  height: 100vh;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 3rem;

  ${({ isNavBarOpen }) =>
    !isNavBarOpen
      ? media.mobile`
      display: none;
   `
      : media.mobile`
     padding-top: 56px;
     border-radius: 0;
   `}
`;

export const Logo = styled.div`
  height: 10rem;

  ${media.mobile`
    display: none;
  `}
`;

export const SeparateLine = styled.div`
  width: 70%;
  height: 1px;
  background-color: ${({ theme }) => theme.colors.main_dark};

  ${media.mobile`
    display: none;
  `}
`;

export const CloseButton = styled.div`
  font-size: 5rem;
`;

export const Nav = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  height: calc(100% - 10rem);
  padding: 30px 18px;

  ${media.mobile`
    height:100%;
  `}
`;

export const Links = styled.div``;
