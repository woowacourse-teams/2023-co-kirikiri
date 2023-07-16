import { styled } from 'styled-components';
import media from '@styles/media';

export const Layout = styled.div`
  display: flex;
  flex-direction: column;
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

export const ChildrenLayout = styled.div`
  margin-left: 15rem;
  padding: 0 18px;

  ${media.mobile`
    margin-left: 0;
  `}
`;
