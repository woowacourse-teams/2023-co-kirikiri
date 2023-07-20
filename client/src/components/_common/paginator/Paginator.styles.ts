import media from '@/styles/media';
import { styled } from 'styled-components';

export const PaginatorWrapper = styled.div`
  display: flex;
  justify-content: center;
`;

export const Paginator = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 50rem;
  height: 6rem;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 3rem;
  box-shadow: ${({ theme }) => theme.shadows.text};

  ${media.mobile`
    width:100%;
  `}
`;

export const PageNumbers = styled.div`
  ${({ theme }) => theme.fonts.button1}
  display: flex;
  column-gap: 0.4rem;
  align-items: center;
  justify-content: center;

  margin: 0 1rem;
`;

export const PageNumber = styled.div<{ isCurPageNumber: boolean }>`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 3.5rem;
  height: 3.5rem;

  color: ${({ theme, isCurPageNumber }) => isCurPageNumber && theme.colors.white};

  background-color: ${({ theme, isCurPageNumber }) =>
    isCurPageNumber && theme.colors.main_dark};
  border-radius: 50%;
`;
