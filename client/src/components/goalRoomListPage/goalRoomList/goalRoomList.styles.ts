import styled from 'styled-components';

export const ListContainer = styled.section`
  min-height: 100vh;
  margin-top: 1rem;
`;

export const ItemContainer = styled.article`
  width: 100%;
  min-width: 35rem;
  height: fit-content;
  padding: 1.7rem 4rem 3rem;

  background-color: ${({ theme }) => theme.colors.white};
  border-radius: 16px;
  box-shadow: ${({ theme }) => theme.shadows.box};
`;

export const FilterBar = styled.div`
  display: flex;
  justify-content: space-between;

  width: 100%;
  margin-right: 9rem;
  margin-bottom: 6rem;

  & > p {
    ${({ theme }) => theme.fonts.button1};
    color: ${({ theme }) => theme.colors.gray300};
  }
`;

export const ListWrapper = styled.section`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 5rem;

  @media (max-width: 660px) {
    grid-template-columns: repeat(1, 1fr);
  }
`;

export const Recruiting = styled.div`
  ${({ theme }) => theme.fonts.description4}
  color: ${({ theme }) => theme.colors.gray300};
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

export const Participant = styled.div`
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

  width: 30rem;
  height: 5rem;
  padding: 1rem 0;

  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme }) => theme.colors.main_middle};
  border-radius: 8px;
`;

export const FilterWrapper = styled.div`
  position: relation;
  ${({ theme }) => theme.fonts.description4};
  position: relative;

  display: flex;
  flex-direction: column;

  color: ${({ theme }) => theme.colors.gray300};
`;

export const FilterTrigger = styled.button`
  ${({ theme }) => theme.fonts.button1};
  width: 12rem;
  height: 1.5rem;
  color: ${({ theme }) => theme.colors.gray300};
  text-align: end;
`;

export const FilterOptionWrapper = styled.ul`
  position: absolute;
  top: -1rem;

  width: 100%;

  border: 0.2rem solid ${({ theme }) => theme.colors.main_middle};
  border-radius: 4px;
`;

export const FilterOption = styled.li`
  cursor: pointer;

  width: 100%;
  height: 50%;
  padding: 1rem 1rem;

  color: ${({ theme }) => theme.colors.white};
  text-align: center;

  background-color: ${({ theme }) => theme.colors.main_dark};
`;
