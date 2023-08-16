import styled from 'styled-components';

export const LoginPageWrapper = styled.div`
  position: relative;

  display: flex;
  align-items: center;
  justify-content: center;

  height: 100vh;

  @keyframes float {
    0% {
      transform: translateY(0);
    }
    50% {
      transform: translateY(-20px);
    }
    100% {
      transform: translateY(0);
    }
  }
`;

export const ElephantImage = styled.img`
  position: absolute;
  top: 10%;
  right: 10%;

  width: 200px;
  height: 200px;

  background-repeat: no-repeat;
  background-size: contain;

  animation: float 5s ease-in-out infinite;
`;
