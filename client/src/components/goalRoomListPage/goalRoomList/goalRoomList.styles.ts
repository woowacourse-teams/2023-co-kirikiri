import media from '@/styles/media';
import styled from 'styled-components';

export const ListContainer = styled.section`
  margin-top: 6rem;
`;

export const ItemContainer = styled.article`
  width: 40.7rem;
  height: fit-content;
  padding: 1.7rem 4rem 3rem;

  background-color: ${({ theme }) => theme.colors.white};
  border-radius: 34px;
  box-shadow: -1.5653846263885498px 7.826924800872803px 46.961544036865234px 0px
    rgba(0, 0, 0, 0.13);
`;

export const FilterBar = styled.div`
  display: flex;
  justify-content: space-between;
  margin-right: 9rem;
  margin-bottom: 4rem;

  & > p {
    ${({ theme }) => theme.fonts.description4};
    color: ${({ theme }) => theme.colors.gray200};
  }
`;

export const ListWrapper = styled.div`
  display: grid;
  grid-row-gap: 6rem;
  grid-template-columns: repeat(2, 1fr);
  min-height: 100vh;

  ${media.mobile`
  grid-template-columns: repeat(1, 1fr);

  `}
`;

export const Recruiting = styled.div`
  ${({ theme }) => theme.fonts.description4}
  color: ${({ theme }) => theme.colors.gray200};
  float: right;
`;

export const Name = styled.h1`
  ${({ theme }) => theme.fonts.description5}
  color: ${({ theme }) => theme.colors.black};
  margin-top: 0.8rem;
`;

export const RoadmapCreator = styled.div`
  ${({ theme }) => theme.fonts.description1}
  color: ${({ theme }) => theme.colors.gray200};
  margin-top: 0.5rem;
`;

export const Wrapper = styled.div`
  display: flex;
  align-items: center;
`;

export const Period = styled.div`
  ${({ theme }) => theme.fonts.description2};
  display: flex;
  align-items: center;
  margin-top: 1.6rem;

  & > p {
    ${({ theme }) => theme.fonts.description3}
    margin-left: 4.3rem;
  }
`;

export const Particpant = styled.div`
  ${({ theme }) => theme.fonts.description2};
  display: flex;
  align-items: center;
  margin-top: 0.5rem;

  & > p {
    ${({ theme }) => theme.fonts.description3}
    margin-left: 4.3rem;
  }
`;

export const CreateGoalRoomButton = styled.button`
  ${({ theme }) => theme.fonts.h1}
  position: fixed;
  top: 1rem;
  left: 50%;
  transform: translate(-50%);

  width: 50%;
  height: 5rem;

  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme }) => theme.colors.main_middle};
  border-radius: 20px;
`;

export const FilterWrapper = styled.div`
  ${({ theme }) => theme.fonts.description4};
  position: relative;
  display: flex;
  flex-direction: column;
  color: ${({ theme }) => theme.colors.gray200};
`;

export const FilterTrigger = styled.button`
  ${({ theme }) => theme.fonts.description4};
  width: 9rem;
  height: 1.5rem;
  color: ${({ theme }) => theme.colors.gray200};
`;

export const FilterOptionWrapper = styled.ul`
  position: absolute;
  top: 2rem;

  width: 9rem;
  /* height: 3rem; */

  border: 0.2rem solid ${({ theme }) => theme.colors.main_middle};
  border-radius: 4px;
`;

export const FilterOption = styled.li`
  cursor: pointer;
  width: 100%;
  height: 50%;
  padding: 1rem 1rem;
`;
