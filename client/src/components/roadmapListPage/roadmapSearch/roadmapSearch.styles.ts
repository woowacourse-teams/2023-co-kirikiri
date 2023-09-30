import media from '@/styles/media';
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

export const RoadmapList = styled.div`
  display: grid;
  grid-gap: 3rem;
  grid-template-columns: 1fr 1fr;
  justify-items: center;

  margin-bottom: 8rem;
  min-height: 100vh;

  ${media.mobile`
  grid-template-columns: 1fr;
  
  `}
`;

export const CreateRoadmapButton = styled.button`
  ${({ theme }) => theme.fonts.nav_title}
  position: fixed;
  right: 5%;
  bottom: 1rem;

  width: 5rem;
  height: 5rem;

  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme }) => theme.colors.main_middle};
  border-radius: 50%;
`;

export const InputFlex = styled.form`
  display: flex;
  align-items: flex-end;
`;

export const ResetSearchButton = styled.button`
  ${({ theme }) => theme.fonts.description3}
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const NoResultWrapper = styled.h1`
  ${({ theme }) => theme.fonts.title_large};
  width: 100%;
  height: 10rem;

  text-align: center;

  display: flex;
  justify-content: center;
  align-items: center;

  color: ${({ theme }) => theme.colors.main_dark};
`;
