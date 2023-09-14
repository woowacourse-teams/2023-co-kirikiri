import styled from 'styled-components';

export {
  InfoText,
  StyledLink,
} from '@components/signUpPage/signUpForm/SignUpForm.styles';

export const OathButtonContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  margin-top: 2rem;
`;

export const OathButton = styled.button<{ type?: string }>`
  ${({ theme }) => theme.fonts.button1}
  display: flex;
  align-items: center;
  justify-content: center;

  width: 100%;
  height: 4rem;
  margin-top: 1rem;
  padding: 1rem;

  color: ${({ type, theme }) => type === 'naver' && theme.colors.white};

  background: ${({ type, theme }) =>
    type === 'naver' ? '#5AC466' : theme.colors.main_dark};
  border: none;
  border-radius: 1rem;

  transition: transform 0.2s ease-in-out;

  &:hover {
    transform: scale(1.04);
  }

  & > span {
    margin-left: ${({ type }) => type === 'naver' && '1rem'};
  }
`;
