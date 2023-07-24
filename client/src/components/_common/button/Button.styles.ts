import styled from 'styled-components';

export const Button = styled.button`
  ${({ theme }) => theme.fonts.button1}
  width: 100%;
  height: 4rem;
  margin-top: 1.5rem;

  color: ${({ theme }) => theme.colors.gray100};

  background: ${({ theme }) => theme.colors.main_dark};
  border-radius: 1rem;

  &:hover {
    opacity: 0.9;
  }
`;
