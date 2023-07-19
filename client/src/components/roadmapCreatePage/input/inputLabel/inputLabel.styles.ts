import styled from 'styled-components';

export const InputLabel = styled.h2`
  display: flex;
  margin-bottom: 2.7rem;
  ${({ theme }) => theme.fonts.title_large}
  color: ${({ theme }) => theme.colors.black};

  > p {
    color: red;
  }
`;
