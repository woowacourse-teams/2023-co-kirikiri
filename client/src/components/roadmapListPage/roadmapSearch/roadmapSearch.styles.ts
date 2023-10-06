import media from '@/styles/media';
import styled from 'styled-components';

export const Wrapper = styled.section`
  display: flex;
  align-items: center;
  justify-content: space-between;

  width: 100%;
  height: 6rem;
  padding-right: 2rem;
  padding-left: 1rem;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 15px;
`;

export const SelectWrapper = styled.div`
  ${({ theme }) => theme.fonts.description5}
  display: flex;
  align-items: center;

  margin-left: 3rem;

  color: ${({ theme }) => theme.colors.gray200};

  flex-grow: 1;
`;

export const TriggerAndOptionWrapper = styled.div`
  position: relative;
  display: flex;
  flex-direction: column;
`;

export const SelectTrigger = styled.li`
  ${({ theme }) => theme.fonts.description5}
  color: ${({ theme }) => theme.colors.main_dark};

  display: flex;
  justify-content: space-between;

  width: 13rem;
  margin-right: 2rem;
`;

export const SearchCategoryOptionGroup = styled.ul`
  position: absolute;
  top: 3rem;
  left: -2rem;

  display: flex;
  flex-direction: column;
  justify-content: space-between;

  margin-right: 2rem;
`;

export const SearchCategoryOption = styled.li<{ isSelected?: boolean }>`
  ${({ theme }) => theme.fonts.description5}
  color: ${({ theme, isSelected }) =>
    isSelected !== undefined && isSelected
      ? theme.colors.main_dark
      : theme.colors.gray300};

  cursor: pointer;

  padding: 1.5rem 3rem;
  width: 15rem;

  background-color: ${({ theme }) => theme.colors.gray100};
`;

export const InputWrapper = styled.div`
  display: inline;
  width: 100%;
`;

export const SearchInput = styled.input`
  ${({ theme }) => theme.fonts.description5}
  float: inline-end;
  width: 100%;
`;

export const SearchButton = styled.button``;

export const SearchSelect = styled.div`
  ${({ theme }) => theme.fonts.description5}
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
