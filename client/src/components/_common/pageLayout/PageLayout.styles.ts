import { styled } from 'styled-components';
import media from '@styles/media';

export const Layout = styled.div`
  width: 100%;
`;

export const NavBar = styled.nav`
  width: 15rem;
  height: 100vh;
  background: ${({ theme }) => theme.colors.gray100};
  border-radius: 3rem;

  ${media.mobile`
    display: none;
  `}
`;
