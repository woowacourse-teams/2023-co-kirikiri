import { styled } from 'styled-components';

export const RoadmapItem = styled.div<{ hasBorder: boolean }>`
  flex-shrink: 0;

  width: 30rem;
  height: fit-content;
  padding: 3rem 2.6rem 3rem 2.6rem;

  background: ${({ theme }) => theme.colors.white};
  border-radius: 18px;
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
`;

export const ReviewersCount = styled.div`
  ${({ theme }) => theme.fonts.button1}
`;

export const RoadmapTitle = styled.div`
  ${({ theme }) => theme.fonts.h1}
`;

export const Description = styled.div`
  ${({ theme }) => theme.fonts.description5}
  margin: 1.6rem 0 3rem 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
`;

export const ExtraHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-evenly;

  width: 100%;
  height: 3rem;
  margin-bottom: 1rem;

  background: ${({ theme }) => theme.colors.gray100};
  border-radius: 8px;
`;

export const ExtraHeaderText = styled.span`
  width: 30%;
  text-align: center;
`;

export const RecommendedRoadmapPeriodNumber = styled.span`
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const ItemExtraInfos = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-around;

  width: 100%;
  height: 10rem;

  background: ${({ theme }) => theme.colors.gray100};
  border-radius: 8px;
`;

export const ExtraInfoBox = styled.div`
  ${({ theme }) => theme.fonts.description4}
  display: flex;
  align-items: center;
  justify-content: center;

  width: 8rem;
  height: 8rem;

  background-color: ${({ theme }) => theme.colors.white};
  border-radius: 8px;
`;

export const Difficulty = styled(ExtraInfoBox)`
  & > div {
    margin-top: 3rem;
  }
`;

export const RecommendedRoadmapPeriod = styled(ExtraInfoBox)`
  ${({ theme }) => theme.fonts.h2}
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
    border-radius: 12px;
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
