import styled from 'styled-components';

export const Wrapper = styled.div`
  color: ${({ theme }) => theme.colors.gray300};

  ${({ theme }) => theme.fonts.button1}
`;
