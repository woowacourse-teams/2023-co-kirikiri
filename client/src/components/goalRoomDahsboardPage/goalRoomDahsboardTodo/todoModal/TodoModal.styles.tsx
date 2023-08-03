import styled from 'styled-components';

export const ModalWrapper = styled.div`
  position: fixed;
  z-index: 100;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);

  width: 45%;
  padding: 2rem;

  background-color: ${({ theme }) => theme.colors.gray_light};
  border-radius: 10px;
  box-shadow: ${({ theme }) => theme.shadows.modal};
`;

export const ModalHeader = styled.h1`
  ${({ theme }) => theme.fonts.h1};
  text-align: center;
  margin-bottom: 2rem;
`;

export const NewTodoText = styled.h3`
  ${({ theme }) => theme.fonts.description2};
  margin-bottom: 1rem;
`;
