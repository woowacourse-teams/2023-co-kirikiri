import styled from 'styled-components';

export const Wrapper = styled.section`
  display: flex;
  align-items: center;
  justify-content: space-around;

  width: 60%;
  height: 6rem;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 30px;
`;

export const InputWrapper = styled.div`
  display: inline;
  width: 40rem;
`;

export const SearchInput = styled.input`
  ${({ theme }) => theme.fonts.description5}
  float: inline-end;
  width: 100%;
`;

export const SearchButton = styled.button``;

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
