import { styled } from 'styled-components';
import media from '@styles/media';

export const Categories = styled.div`
  display: flex;
  flex-wrap: wrap;
  row-gap: 1.4rem;
  column-gap: 1.4rem;
  align-items: center;
  justify-content: center;

  width: 100%;
  padding: 1.8rem 2.5rem;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 12px;
`;

export const CategoriesRow = styled.div`
  display: flex;
  column-gap: 1.4rem;
`;

export const Category = styled.button<{ selected: boolean }>`
  ${({ theme }) => theme.fonts.description5}
  cursor: pointer;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  width: 8.2rem;
  height: 7.8rem;

  color: ${({ theme, selected }) => selected && theme.colors.main_dark};

  background-color: white;
  border-radius: 8px;
  box-shadow: ${({ theme }) => theme.shadows.box};

  &:hover {
    transform: scale(1.05);
    color: ${({ theme }) => theme.colors.main_dark};
    transition: all ease-in-out 0.1s;
  }
`;

export const CategoryName = styled.div`
  ${({ theme }) => theme.fonts.description4}
  margin-top: 0.3rem;

  ${media.tablet`
    display:none;
  `}
`;
