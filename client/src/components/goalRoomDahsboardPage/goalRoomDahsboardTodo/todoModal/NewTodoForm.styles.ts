import styled from 'styled-components';

export const AddingTodoForm = styled.form`
  display: flex;
  align-items: center;

  padding: 1rem;

  border: 1px solid ${({ theme }) => theme.colors.gray200};
  border-radius: 10px;
  box-shadow: ${({ theme }) => theme.shadows.main};
`;

export const InputContainer = styled.div`
  display: flex;
  flex-direction: column;
  margin-top: 1rem;
`;

export const ContentInputContainer = styled(InputContainer)`
  flex: 4;
`;

export const DateInputContainer = styled(InputContainer)`
  flex: 1;
`;

export const TodoDateInput = styled.input`
  display: block;
  flex: 1;

  width: 100%;
  margin-top: 1rem;
  padding: 0.5rem;

  font-size: 1rem;

  border-bottom: 1px solid ${({ theme }) => theme.colors.gray200};
  border-radius: 5px;
`;

export const ModalContentInput = styled(TodoDateInput)`
  flex: 3;
`;

export const DateInputLabel = styled.label`
  ${({ theme }) => theme.fonts.caption1};
  margin-bottom: 0.3rem;
  color: ${({ theme }) => theme.colors.gray300};
  text-align: center;
`;

export const SubmitButton = styled.button`
  cursor: pointer;

  flex: 0.4;

  margin: 1rem 0 0 2rem;
  padding: 0.5rem;

  color: ${({ theme }) => theme.colors.white};
  text-align: center;

  background-color: ${({ theme }) => theme.colors.main_middle};
  border: none;
  border-radius: 5px;

  &:hover {
    background-color: ${({ theme }) => theme.colors.main_dark};
  }

  &:disabled {
    cursor: not-allowed;
    background-color: #bbbbbb;
  }
`;

export const Error = styled.span`
  ${({ theme }) => theme.fonts.caption1};
  color: ${({ theme }) => theme.colors.red};
`;
