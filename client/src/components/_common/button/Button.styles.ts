import styled from 'styled-components';

export const Button = styled.button<{ variant: string }>`
  ${({ theme }) => theme.fonts.button1}
  width: 100%;
  height: 4rem;
  margin-top: 1.5rem;

  color: ${({ theme }) => theme.colors.gray100};

  background: ${({ variant, theme }) =>
    variant === 'primary' ? theme.colors.main_dark : theme.colors.red};
  border-radius: 8px;

  &:hover {
    opacity: 0.9;
  }
`;
