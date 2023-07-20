import styled from 'styled-components';

export const InputDescription = styled.div`
  ${({ theme }) => theme.fonts.description4}
  margin-bottom: 2.5rem;
  color: ${({ theme }) => theme.colors.gray300};
`;
