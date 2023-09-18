import {
  ModalHeader,
  ModalWrapper,
  NewTodoText,
} from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/todoModal/TodoModal.styles';
import styled from 'styled-components';

export const CertificationModalWrapper = styled(ModalWrapper)`
  max-height: 80vh;
  overflow-y: scroll;
`;

export const CertificationHeader = styled(ModalHeader)``;

export const CertificationText = styled(NewTodoText)``;

export const PreviewImage = styled.img`
  width: 19rem;
  height: 20rem;

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

  width: 19rem;
  height: 20rem;

  border: dashed 0.2rem ${({ theme }) => theme.colors.gray200};
  border-radius: 10px;
`;

export const PlusButton = styled.span`
  ${({ theme }) => theme.fonts.h2};
  color: ${({ theme }) => theme.colors.gray200};
`;

export const PreviewWrapper = styled.div`
  position: relative;
  width: 19rem;
  height: 20rem;
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
  width: 19rem;
  margin-top: 1rem;
  padding: 1rem;

  border: 1px solid ${({ theme }) => theme.colors.gray200};
  border-radius: 10px;
`;

export const CertificationSubmitButton = styled.button`
  width: 19rem;
  padding: 1rem;
  background: ${({ theme }) => theme.colors.main_middle};
  border-radius: 10px;
`;

export const CertificationFeedsWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  justify-content: flex-start;
`;

export const CertificationFeedCard = styled.div`
  flex: 0 0 calc(33.33% - 1.5rem);

  width: 20rem;
  height: 35rem;

  border-radius: 10px;
  box-shadow: ${({ theme }) => theme.shadows.threeD};

  transition: transform 0.2s;

  &:hover {
    transform: translateY(-5px);
  }
`;

export const CertificationFeedImage = styled.img`
  width: 100%;
  height: 20rem;
  object-fit: cover;
`;

export const CertificationFeedDescription = styled.p`
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
  color: ${({ theme }) => theme.colors.gray600};
`;

export const CertificationFeedsUserInfo = styled.div`
  display: flex;
  align-items: center;
  padding: 0.5rem 1rem;
`;

export const CertificationFeedsUserImage = styled.img`
  width: 4rem;
  height: 4rem;
  margin-right: 0.5rem;
  border-radius: 50%;
`;

export const CertificationFeedsUserName = styled.span`
  ${({ theme }) => theme.fonts.h2}
  color: ${({ theme }) => theme.colors.black};
`;

export const CreatedAtText = styled.span`
  font-size: 0.8rem;
  color: ${({ theme }) => theme.colors.gray300};
  text-align: center;
`;
