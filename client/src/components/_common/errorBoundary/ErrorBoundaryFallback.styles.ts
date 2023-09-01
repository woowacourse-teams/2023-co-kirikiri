import styled from 'styled-components';

export const ErrorBoundaryFallbackWrapper = styled.main`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 100%;
  height: 100vh;
`;

export const FallbackContent = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
`;

export const CryingElephant = styled.img`
  width: 20rem;
  height: 20rem;
  margin-bottom: 2rem;
`;

export const FallbackErrorMessage = styled.span`
  ${({ theme }) => theme.fonts.description5};
  margin-bottom: 2rem;
`;
