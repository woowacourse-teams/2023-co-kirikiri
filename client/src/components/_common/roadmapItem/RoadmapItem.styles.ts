import { styled } from 'styled-components';

export const RoadmapItem = styled.div`
  flex-shrink: 0;

  width: 100%;
  max-width: 36.8rem;
  height: fit-content;
  padding: 3rem 2.6rem 3rem 2.6rem;

  background: ${({ theme }) => theme.colors.white};
  border-radius: 3.1rem;
  box-shadow: ${({ theme }) => theme.shadows.box};
`;

export const ItemHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2.5rem;
`;

export const AchieversCount = styled.div`
  ${({ theme }) => theme.fonts.description2}
  color: ${({ theme }) => theme.colors.gray300};
  text-shadow: ${({ theme }) => theme.shadows.text};
`;

export const ReviewersCount = styled.div`
  ${({ theme }) => theme.fonts.button1}
`;

export const ItemInfos = styled.div``;

export const RoadmapTitle = styled.div`
  ${({ theme }) => theme.fonts.h1}
`;

export const Description = styled.div`
  ${({ theme }) => theme.fonts.description5}
  margin: 1.6rem 0 3rem 0;
`;

export const ItemExtraInfos = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-around;

  width: 100%;
  height: 10rem;

  background: ${({ theme }) => theme.colors.gray100};
  border-radius: 1rem;
`;

export const ExtraInfoCol = styled.div`
  ${({ theme }) => theme.fonts.description4}
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  & > div:first-child {
    display: flex;
    align-items: center;
    justify-content: center;

    width: 8rem;
    height: 8rem;
    margin-bottom: 0.3rem;

    background-color: ${({ theme }) => theme.colors.white};
    border-radius: 2rem;
  }
`;

export const Category = styled.div``;

export const Difficulty = styled.div``;

export const RecommendedRoadmapPeriod = styled.div``;

export const SeeDetailButton = styled.button`
  ${({ theme }) => theme.fonts.button1}
  cursor: pointer;

  width: 100%;
  height: 4rem;
  margin-top: 1.5rem;

  color: ${({ theme }) => theme.colors.gray100};

  background: ${({ theme }) => theme.colors.main_dark};
  border-radius: 1rem;

  &:hover {
    opacity: 0.9;
  }
`;

export const ItemFooter = styled.div`
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-top: 2.5rem;
`;

export const CreatedBy = styled.div`
  ${({ theme }) => theme.fonts.description4}
  color: ${({ theme }) => theme.colors.gray300};
  text-shadow: ${({ theme }) => theme.shadows.text};
`;

export const Tags = styled.div`
  ${({ theme }) => theme.fonts.description4}
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  color: ${({ theme }) => theme.colors.main_dark};
`;
