import { styled } from 'styled-components';

export const RoadmapListView = styled.div`
  display: flex;
  flex-direction: column;
  row-gap: 2.5rem;
  margin-top: 8rem;
`;

export const ListTitle = styled.h1`
  ${({ theme }) => theme.fonts.title_large}
  display: flex;
  margin-left: 2rem;
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const SelectWrapper = styled.div`
  ${({ theme }) => theme.fonts.description5}
  display: flex;
  align-items: center;

  margin-bottom: -2rem;
  margin-left: 3rem;

  color: ${({ theme }) => theme.colors.gray200};
`;

export const SearchCategoryOptionGroup = styled.ul`
  display: flex;
  justify-content: space-between;
  width: 30rem;
  margin-right: 2rem;
`;

export const SearchCategoryOption = styled.li<{ isSelected?: boolean }>`
  ${({ theme }) => theme.fonts.description5}
  color: ${({ theme, isSelected }) =>
    isSelected !== undefined && isSelected
      ? theme.colors.white
      : theme.colors.main_middle};

  cursor: pointer;

  border: 0.2rem solid ${({ theme }) => theme.colors.main_dark};
  border-radius: 20px;

  padding: 0 1rem;

  background-color: ${({ theme, isSelected }) =>
    isSelected !== undefined && isSelected ? theme.colors.main_dark : theme.colors.white};
`;
