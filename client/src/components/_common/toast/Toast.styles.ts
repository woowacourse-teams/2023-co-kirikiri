import styled from 'styled-components';

type ToastContainerProps = {
  isError: boolean;
};

export const ToastContainer = styled.div<ToastContainerProps>`
  ${({ theme }) => theme.fonts.description3}
  position: fixed;
  right: 0;
  bottom: 0;

  display: flex;
  align-items: center;
  justify-content: flex-start;

  width: 25%;
  min-width: 15rem;
  max-width: 50rem;
  margin: 1rem;
  padding: 1.5rem;

  color: ${(props) => props.theme.colors.white};

  background-color: ${(props) =>
    props.isError ? '#F16666' : props.theme.colors.main_dark};
  border-radius: 12px;
  box-shadow: ${({ theme }) => theme.shadows.box};
`;
export const ToastMessageContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-left: 2rem;
`;

export const ToastMessage = styled.span`
  margin-right: 0.8rem;
  color: ${({ theme }) => theme.zIndex.toast};
`;

export const ShowUpRoot = styled.div`
  position: fixed;
  z-index: ${({ theme }) => theme.colors.white};
  bottom: 2rem;

  display: flex;
  justify-content: center;

  width: 100%;

  -webkit-animation: slide-in-blurred-bottom 1.5s cubic-bezier(0.23, 1, 0.32, 1) 1
    alternate both;
  animation: slide-in-blurred-bottom 1.5s cubic-bezier(0.23, 1, 0.32, 1) 1 alternate both;

  @keyframes slide-in-blurred-bottom {
    0% {
      transform: translateY(1000px) scaleY(2.5) scaleX(0.2);
      filter: blur(40px);
    }
    100% {
      transform: translateY(0) scaleY(1) scaleX(1);
      filter: blur(0);
    }
  }
`;

export const ShowDownRoot = styled.div`
  position: fixed;
  z-index: ${({ theme }) => theme.zIndex.toast};
  bottom: 2rem;

  display: flex;
  justify-content: center;

  width: 100%;

  -webkit-animation: slide-in-blurred-bottom 1s cubic-bezier(0.23, 1, 0.32, 1) 1
    alternate-reverse both;
  animation: slide-in-blurred-bottom 1s cubic-bezier(0.23, 1, 0.32, 1) 1 alternate-reverse
    both;

  @keyframes slide-in-blurred-bottom {
    0% {
      -webkit-transform-origin: 50% 100%;
      transform-origin: 50% 100%;
      -webkit-transform: translateY(1000px) scaleY(2.5) scaleX(0.2);
      transform: translateY(1000px) scaleY(2.5) scaleX(0.2);

      display: none;

      -webkit-filter: blur(40px);
      filter: blur(40px);
    }
    50% {
      -webkit-transform-origin: 50% 50%;
      transform-origin: 50% 50%;
      -webkit-transform: translateY(0) scaleY(1) scaleX(1);
      transform: translateY(0) scaleY(1) scaleX(1);

      display: flex;

      -webkit-filter: blur(0);
      filter: blur(0);
    }
  }
`;
