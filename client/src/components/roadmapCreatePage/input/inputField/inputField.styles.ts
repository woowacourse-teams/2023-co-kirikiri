import styled from 'styled-components';

export const Input = styled.textarea`
  width: 80%;
  height: 80%;
  color: ${({ theme }) => theme.colors.gray300};
  &::placeholder {
    color: ${({ theme }) => theme.colors.gray200};
    ${({ theme }) => theme.fonts.description4};
  }
`;
