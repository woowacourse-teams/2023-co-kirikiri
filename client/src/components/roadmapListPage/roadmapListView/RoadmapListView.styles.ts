import { styled } from 'styled-components';

export const RoadmapListView = styled.div`
  display: flex;
  flex-direction: column;
  row-gap: 2.5rem;
  margin-top: 8rem;
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

export const ServiceDescWrapper = styled.div`
  display: flex;
  justify-content: center;
  width: 100%;
  margin: 2rem 0 2rem 0;
`;

export const ServiceDescContent = styled.div`
  width: 70rem;
  padding: 1rem;
  background: ${({ theme }) => theme.colors.gray100};
  border-radius: 10px;
`;

export const ServiceDesc = styled.p`
  ${({ theme }) => theme.fonts.description3};
  text-align: center;
  line-height: 2;
`;
