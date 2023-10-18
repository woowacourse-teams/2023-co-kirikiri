import { styled, css } from 'styled-components';
import media from '@styles/media';
import { Link, NavLink } from 'react-router-dom';

export const NavBar = styled.nav<{ isNavBarOpen: boolean }>`
  position: fixed;
  z-index: ${({ theme }) => theme.zIndex.navBar};

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

  ${({ isNavBarOpen }) =>
    media.mobile(css`
      top: 9rem;
      display: ${isNavBarOpen ? 'flex' : 'none'};
      width: 60%;
      height: calc(100vh - 9rem);

      &:hover {
        width: 60%;
      }
    `)}
`;

export const NavTitle = styled(Link)`
  ${({ theme }) => theme.fonts.nav_title};
  padding-top: 2rem;
`;

export const Item = styled(NavLink)`
  ${({ theme }) => theme.fonts.nav_text};
  display: flex;
  align-items: center;
  width: 100%;
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
  display: flex;
  align-items: center;
  justify-content: center;
  height: 10rem;

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
`;

export const Links = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;
