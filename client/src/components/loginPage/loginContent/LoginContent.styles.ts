import { SingleCardWrapper } from '@components/_common/SingleCard/SingleCard.styles';
import styled from 'styled-components';

export { InfoText, FormList } from '@components/signUpPage/signUpForm/SignUpForm.styles';

export const LoginContentWrapper = styled(SingleCardWrapper)``;

export const BackGuide = styled.button`
  ${({ theme }) => theme.fonts.small_bold};
  display: flex;
  align-items: center;
  justify-content: flex-start;

  width: 100%;
  margin-top: 1rem;

  color: ${({ theme }) => theme.colors.main_dark};

  p {
    margin-left: 0.8%;
  }
`;
