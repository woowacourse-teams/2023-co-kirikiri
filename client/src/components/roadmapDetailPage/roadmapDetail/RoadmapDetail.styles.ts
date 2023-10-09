import styled from 'styled-components';

export const RoadmapDetail = styled.div`
  width: 90%;
  padding: 2rem 0 4rem 0;
`;

export const RoadmapInfo = styled.div``;

export const Title = styled.div`
  ${({ theme }) => theme.fonts.title_large};
  display: flex;
  align-items: flex-end;
  margin-bottom: 2rem;
  color: ${({ theme }) => theme.colors.main_dark};

  & > p {
    ${({ theme }) => theme.fonts.description4}
    margin-bottom: 0.5rem;
    margin-left: 2rem;
    color: ${({ theme }) => theme.colors.gray300};
  }
`;

export const Tags = styled.ul`
  ${({ theme }) => theme.fonts.description4}
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: flex-start;

  width: 100%;
  padding: 0.3rem 1rem;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 5px;

  color: ${({ theme }) => theme.colors.gray300};

  & > li {
    margin-right: 1rem;
  }
`;

export const Introduction = styled.article`
  ${({ theme }) => theme.fonts.description4};
  min-height: 9.5rem;

  & > p {
    margin-top: 1.6rem;
  }

  & div {
    ${({ theme }) => theme.fonts.nav_title};
    margin-top: 2rem;
    color: ${({ theme }) => theme.colors.main_middle};
  }
`;

export const Body = styled.article`
  ${({ theme }) => theme.fonts.description4};
  min-height: 25.6rem;

  & > p {
    margin-top: 1.6rem;
  }

  & div {
    ${({ theme }) => theme.fonts.nav_title};
    margin-top: 2.7rem;
    color: ${({ theme }) => theme.colors.main_middle};
  }
`;

export const Description = styled.div`
  display: flex;
  flex-direction: column;
`;

export const Buttons = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 100%;
  margin: 2rem 0;

  border-radius: 8px;

  & > div {
    width: 0.2rem;
    height: 5.5rem;
    background-color: ${({ theme }) => theme.colors.white};
  }
`;

export const Button = styled.button`
  ${({ theme }) => theme.fonts.description3}
  width: 30%;
  height: 4rem;

  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme }) => theme.colors.main_dark};
  border-radius: 10px;
`;
