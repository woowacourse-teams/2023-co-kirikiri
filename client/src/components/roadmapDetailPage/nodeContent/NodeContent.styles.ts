import styled from 'styled-components';
import media from '@styles/media';

export const SliderContent = styled.div`
  display: flex;
  /* aspect-ratio: 5 / 3.5; */
  height: 30rem;
  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 8px;

  ${media.mobile`
    aspect-ratio: 0;
  `}
`;

export const LeftContent = styled.div`
  width: 35%;

  ${media.mobile`
    display: none;
  `}
`;

export const NodeImg = styled.img`
  width: 100%;
  height: 100%;
  padding: 1.5rem;
  object-fit: cover;
`;

export const NoImg = styled.div`
  ${({ theme }) => theme.fonts.title_large}
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  width: 100%;
  height: 100%;
`;

export const Separator = styled.div`
  display: flex;
  flex-direction: column;
  width: 0.2rem;
  height: 100%;

  & > div {
    height: 30%;
  }

  & > div:nth-child(2) {
    height: 40%;
    background-color: black;
  }
`;

export const RightContent = styled.div`
  ${({ theme }) => theme.fonts.description4}
  overflow: scroll;
  width: 65%;
  padding: 1.5rem;
  padding-top: 3rem;

  & > p {
    margin-left: 4rem;
  }

  ${media.mobile`
    width: 100%;
    height: 60rem;
    padding-top: 1.5rem;
  `}
`;

export const ContentTitle = styled.div`
  ${({ theme }) => theme.fonts.nav_title}
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const Step = styled.div`
  ${({ theme }) => theme.fonts.nav_title}
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;

  width: 3rem;
  height: 3rem;
  margin-right: 0.5rem;

  border-radius: 50%;
`;
