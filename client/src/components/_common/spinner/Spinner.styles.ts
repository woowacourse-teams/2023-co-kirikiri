import styled from 'styled-components';
import { spin } from '@styles/animations';

export const Spinner = styled.div`
  width: 120px;
  height: 120px;
  margin: 60px auto;

  border: 16px solid ${({ theme }) => theme.colors.gray300};
  border-top: 16px solid ${({ theme }) => theme.colors.main_dark};
  border-radius: 50%;

  animation: ${spin} 2s linear infinite;
`;
