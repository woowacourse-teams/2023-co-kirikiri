import styled from 'styled-components';

export {
  FormItem,
  SubmitButton,
} from '@components/signUpPage/signUpForm/SignUpForm.styles';

export const LoginForm = styled.form`
  width: 100%;
  margin-top: 3rem;
`;

export const FormItemContainer = styled.div`
  overflow: hidden;
  border: 1px solid ${({ theme }) => theme.colors.gray300};
  border-radius: 8px;
`;
