import styled from 'styled-components';

export const Container = styled.section`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  width: 100%;
  height: 100vh;
`;

export const ElephantImage = styled.img`
  width: 20%;
`;

export const NotFoundTitle = styled.h1`
  ${({ theme }) => theme.fonts.title_large};
  font-size: 4rem;
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const NotFoundText = styled.h2`
  ${({ theme }) => theme.fonts.h2};
  color: ${({ theme }) => theme.colors.gray300};
`;

export const ForbiddenTitle = styled.h1`
  ${({ theme }) => theme.fonts.title_large};
  font-size: 4rem;
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const ForbiddenText = styled.h2`
  ${({ theme }) => theme.fonts.h2};
  color: ${({ theme }) => theme.colors.gray300};
`;

export const RuntimeTitle = styled.h1`
  ${({ theme }) => theme.fonts.title_large};
  font-size: 4rem;
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const RuntimeText = styled.h2`
  ${({ theme }) => theme.fonts.h2};
  color: ${({ theme }) => theme.colors.gray300};
`;

export const CriticalTitle = styled.h1`
  ${({ theme }) => theme.fonts.title_large};
  font-size: 4rem;
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const CriticalText = styled.h2`
  ${({ theme }) => theme.fonts.h2};
  color: ${({ theme }) => theme.colors.gray300};
`;

export const MovePageButton = styled.button`
  ${({ theme }) => theme.fonts.button1}
  margin: 2rem 0;
  padding: 1rem 2rem;

  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme }) => theme.colors.main_middle};
  border-radius: 30px;
`;
