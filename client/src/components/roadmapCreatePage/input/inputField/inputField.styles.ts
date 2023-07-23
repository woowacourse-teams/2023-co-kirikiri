import styled from 'styled-components';

export const Input = styled.textarea`
  width: 80%;
  height: 80%;
  color: ${({ theme }) => theme.colors.gray300};
  &::placeholder {
    ${({ theme }) => theme.fonts.description4};
    color: ${({ theme }) => theme.colors.gray200};
  }
`;
