import { styled } from 'styled-components';

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
  border-radius: 2rem;
`;

export const CategoriesRow = styled.div`
  display: flex;
  column-gap: 1.4rem;
`;

export const Category = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  ${({ theme }) => theme.fonts.description5}

  width: 8.2rem;
  height: 7.8rem;

  background-color: white;
  border-radius: 1.4rem;
  box-shadow: ${({ theme }) => theme.shadows.box};
`;

export const CategoryName = styled.div`
  margin-top: 0.5rem;
`;
