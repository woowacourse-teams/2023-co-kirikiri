import styled from 'styled-components';
import { float } from '@styles/animations';

export const LoginPageWrapper = styled.div`
  position: relative;

  display: flex;
  align-items: center;
  justify-content: center;

  height: 100vh;
`;

export const ElephantImage = styled.img`
  position: absolute;
  top: 10%;
  right: 10%;

  width: 200px;
  height: 200px;

  background-repeat: no-repeat;
  background-size: contain;

  animation: ${float} 5s ease-in-out infinite;
`;
