import styled from 'styled-components';

export const InputLabel = styled.h2`
  ${({ theme }) => theme.fonts.title_large}
  display: flex;
  margin-bottom: 2rem;
  color: ${({ theme }) => theme.colors.black};

  > abbr {
    color: ${({ theme }) => theme.colors.red};
  }
`;
