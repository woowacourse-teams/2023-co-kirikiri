import { styled } from 'styled-components';
import media from '@styles/media';
import { Link, NavLink } from 'react-router-dom';
import BREAK_POINTS from '@constants/_common/breakPoints';

export const NavBar = styled.nav<{ isNavBarOpen: boolean }>`
  position: fixed;
  z-index: ${({ theme }) => theme.zIndex.navBar};
  top: 0;

  width: ${({ isNavBarOpen }) => (isNavBarOpen ? '20rem' : '8rem')};
  height: 100vh;
  padding-left: 2rem;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 10px;
  box-shadow: ${({ theme }) => theme.shadows.box};

  transition: width 0.2s;

  &:hover {
    width: 20rem;
  }

  @media screen and (max-width: ${BREAK_POINTS.MOBILE}px) {
    top: 9rem;
    display: ${({ isNavBarOpen }) => (isNavBarOpen ? 'flex' : 'none')};
    width: 60%;

    &:hover {
      width: 60%;
    }
  }
`;

export const NavTitle = styled(Link)`
  ${({ theme }) => theme.fonts.nav_title};
`;

export const Item = styled(NavLink)`
  width: 100%;
  display: flex;
  align-items: center;
  ${({ theme }) => theme.fonts.nav_text};
  color: ${({ theme }) => theme.colors.gray300};

  &.active {
    color: ${({ theme }) => theme.colors.main_dark};
  }

  &:hover {
    color: ${({ theme }) => theme.colors.main_middle};
  }
`;

export const ItemIcon = styled.span`
  display: inline-block;
  width: 2rem;
  margin-right: 0.5rem;
`;

export const UserProfileImage = styled.img`
  width: 100%;
  max-width: 15rem;
  height: auto;
`;

export const Text = styled.span`
  overflow: hidden;

  width: 0;
  margin-left: 4rem;

  white-space: nowrap;

  opacity: 0;

  transition: opacity 0.2s, width 0.2s;

  ${NavBar}:hover & {
    width: auto;
    opacity: 1;
  }

  ${media.mobile`
    width: auto;
    opacity: 1;
    `}
`;

export const Logo = styled.div`
  height: 10rem;
  display: flex;
  justify-content: center;
  align-items: center;

  ${media.mobile`
    display: none;
  `}
`;

export const SeparateLine = styled.div`
  width: 70%;
  height: 0.1rem;
  margin: 1rem auto;
  background-color: ${({ theme }) => theme.colors.main_dark};

  ${media.mobile`
    display: none;
  `}
`;

export const Nav = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: calc(100% - 10rem);

  ${media.mobile`
    height:100%;
  `}
`;

export const Links = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;
