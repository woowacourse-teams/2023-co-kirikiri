import styled from 'styled-components';

export const ExtraInfo = styled.div`
  & > p {
    ${({ theme }) => theme.fonts.nav_title}
    color: ${({ theme }) => theme.colors.main_middle}
  }
`;

export const RoadmapMetadata = styled.div`
  display: flex;
  flex-direction: column;
`;

export const Category = styled.div`
  ${({ theme }) => theme.fonts.description3}
  display: flex;
  align-items: center;
  justify-content: flex-start;

  width: 34.4rem;
  height: 2rem;
  padding: 1.5rem 0;
  padding-left: 2.2rem;
  margin-top: 1.6rem;

  background-color: ${({ theme }) => theme.colors.gray100};

  color: ${({ theme }) => theme.colors.gray300};

  border-radius: 5px;

  & > p {
    color: ${({ theme }) => theme.colors.main_dark};
  }
`;

export const Difficulty = styled.div`
  ${({ theme }) => theme.fonts.description3}
  display: flex;
  align-items: center;
  justify-content: flex-start;

  width: 34.4rem;
  height: 2rem;
  padding: 1.5rem 0;
  padding-left: 2.2rem;
  margin-top: 1.6rem;

  background-color: ${({ theme }) => theme.colors.gray100};

  color: ${({ theme }) => theme.colors.gray300};

  border-radius: 5px;

  & > p {
    color: ${({ theme }) => theme.colors.main_dark};
  }
`;

export const RecommendedRoadmapPeriod = styled.div`
  ${({ theme }) => theme.fonts.description3}
  display: flex;
  align-items: center;
  justify-content: flex-start;

  width: 34.4rem;
  height: 2rem;
  padding: 1.5rem 0;
  padding-left: 2.2rem;
  margin-top: 1.6rem;

  background-color: ${({ theme }) => theme.colors.gray100};

  color: ${({ theme }) => theme.colors.gray300};

  border-radius: 5px;

  & > p {
    color: ${({ theme }) => theme.colors.main_dark};
  }
`;
