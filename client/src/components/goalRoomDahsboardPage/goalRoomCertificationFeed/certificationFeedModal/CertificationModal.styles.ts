import {
  ModalHeader,
  ModalWrapper,
  NewTodoText,
} from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/todoModal/TodoModal.styles';
import styled from 'styled-components';

export const CertificationModalWrapper = styled(ModalWrapper)``;

export const CertificationHeader = styled(ModalHeader)``;

export const CertificationText = styled(NewTodoText)``;

export const PreviewImage = styled.img`
  width: 20rem;
  height: 25rem;

  object-fit: cover;
  border-radius: 10px;
  box-shadow: ${({ theme }) => theme.shadows.threeD};
`;

export const FileUploadCard = styled.label`
  cursor: pointer;

  position: relative;

  display: flex;
  align-items: center;
  justify-content: center;

  width: 20rem;
  height: 25rem;

  border: dashed 0.2rem ${({ theme }) => theme.colors.gray200};
  border-radius: 10px;
`;

export const PlusButton = styled.span`
  ${({ theme }) => theme.fonts.h2};
  color: ${({ theme }) => theme.colors.gray200};
`;

export const PreviewWrapper = styled.div`
  position: relative;
  width: 20rem;
  height: 25rem;
  margin-top: 2rem;
`;

export const PreviewDeleteButton = styled.button`
  position: absolute;
  top: 2rem;
  right: 1rem;

  width: 2rem;
  height: 2rem;

  background: ${({ theme }) => theme.colors.red};
  border-radius: 50%;
  box-shadow: ${({ theme }) => theme.shadows.threeD};

  &:hover {
    box-shadow: ${({ theme }) => theme.shadows.threeDHovered};

    &:hover {
      transform: scale(1.04);
    }
  }
`;

export const ErrorMessage = styled.div`
  height: 2rem;
  margin-top: 1rem;
  color: ${({ theme }) => theme.colors.red};
`;

export const InputFieldWrapper = styled.div`
  width: 20rem;
  margin-top: 1rem;
  padding: 1rem;

  border: 1px solid ${({ theme }) => theme.colors.gray200};
  border-radius: 10px;
`;

export const CertificationSubmitButton = styled.button`
  width: 20rem;
  padding: 1rem;
  background: ${({ theme }) => theme.colors.main_middle};
  border-radius: 10px;
`;
