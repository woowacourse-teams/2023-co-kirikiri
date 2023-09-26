import media from '@styles/media';
import styled from 'styled-components';

export const RoadmapDetail = styled.div`
  position: relative;
  margin: 4rem 0;
  padding: 0 2rem;
  white-space: pre-line;

  ${media.mobile`
    flex-direction: column;
    align-items: center;
  `}
`;

export const RoadmapBody = styled.p`
  ${({ theme }) => theme.fonts.button1}
  width: 50%;
  padding: 4rem 4rem;
  height: 35rem;

  overflow: scroll;

  border-radius: 18px;
  box-shadow: ${({ theme }) => theme.shadows.box};

  color: ${({ theme }) => theme.colors.gray300};

  ${media.mobile`
    padding: 4rem 4rem;
  `}

  & > strong {
    ${({ theme }) => theme.fonts.h1};
    margin-bottom: 4rem;
    color: ${({ theme }) => theme.colors.main_dark};
  }
`;

export const PageOnTop = styled.div`
  display: flex;
  justify-content: space-around;
`;
