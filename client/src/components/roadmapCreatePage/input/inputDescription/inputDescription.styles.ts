import styled from 'styled-components';

export const InputDescription = styled.div`
  margin-bottom: 2.5rem;
  ${({ theme }) => theme.fonts.description5}
  color: ${({ theme }) => theme.colors.gray300};
`;
