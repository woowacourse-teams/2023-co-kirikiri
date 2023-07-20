import styled from 'styled-components';

export const Wrapper = styled.div`
  ${({ theme }) => theme.fonts.button1}
  color: ${({ theme }) => theme.colors.gray300};
`;
