import styled from 'styled-components';

export { InfoText, StyledLink } from '@components/signUpPage/SignUpForm.styles';

export const OathButtonContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  margin-top: 1.5rem;
`;

export const OathButton = styled.button<{ type?: string }>`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 100%;
  margin-top: 1rem;
  padding: 1rem;

  background: ${({ type, theme }) =>
    type === 'kakao'
      ? 'rgb(255, 225, 83)'
      : type === 'google'
      ? theme.colors.gray100
      : theme.colors.main_dark};
  border: none;
  border-radius: 1rem;

  transition: transform 0.2s ease-in-out;

  &:hover {
    transform: scale(1.04);
  }
`;
