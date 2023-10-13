import media from '@/styles/media';
import styled from 'styled-components';

export {
  InfoText,
  StyledLink,
} from '@components/signUpPage/signUpForm/SignUpForm.styles';

export const LoginOptions = styled.div`
  width: 100%;
`;

export const OathButtonContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  margin-top: 3rem;
`;

export const OathButton = styled.button<{ type?: string }>`
  ${({ theme }) => theme.fonts.h2}
  display: flex;
  align-items: center;
  justify-content: center;

  width: 30rem;
  height: 6rem;

  color: ${({ theme }) => theme.colors.white};

  background: ${({ type, theme }) =>
    type === 'naver' ? theme.colors.naver : theme.colors.main_dark};
  border-radius: 8px;

  transition: transform 0.2s ease-in-out;

  &:hover {
    transform: scale(1.04);
  }

  span {
    margin-left: ${({ type }) => type === 'naver' && '1rem'};
  }

  &:not(:last-child) {
    margin-bottom: 2rem;
  }

  ${media.mobile`
    width: 78%;
    height: 7rem;
  `}
`;
