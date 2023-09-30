import styled from 'styled-components';

export const RoadmapDetail = styled.div`
  padding: 2rem 0 4rem 0;
`;

export const RoadmapInfo = styled.div``;

export const Title = styled.div`
  ${({ theme }) => theme.fonts.title_large};
  margin-bottom: 2rem;
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const Description = styled.div`
  display: flex;
`;

export const ButtonsWrapper = styled.div`
  position: relative;
  width: 100%;
`;

export const Buttons = styled.div`
  bottom: 3rem;

  display: flex;
  align-items: center;
  justify-content: space-around;

  margin: 2rem 0;

  background-color: ${({ theme }) => theme.colors.main_dark};
  border-radius: 8px;

  & > div {
    width: 0.2rem;
    height: 5.5rem;
    background-color: ${({ theme }) => theme.colors.white};
  }
`;

export const Button = styled.button`
  ${({ theme }) => theme.fonts.nav_text}
  width: 50%;
  height: 5.5rem;

  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme }) => theme.colors.main_dark};
  border-radius: 8px;
`;
