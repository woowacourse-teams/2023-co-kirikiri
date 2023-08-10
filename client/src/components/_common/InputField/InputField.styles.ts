import styled from 'styled-components';

export const InputField = styled.div``;

export const FieldHeader = styled.div<{ size?: 'small' | 'normal' }>`
  margin-bottom: ${({ size }) => (size === 'small' ? '0.8rem' : '1.8rem')};
`;

export const Label = styled.label<{ size?: 'small' | 'normal' }>`
  ${({ theme, size }) =>
    size === 'small' ? theme.fonts.nav_text : theme.fonts.nav_title};
  & > span {
    color: ${({ theme }) => theme.colors.red};
  }
`;

export const Description = styled.p`
  ${({ theme }) => theme.fonts.h2};
  color: ${({ theme }) => theme.colors.gray300};
`;

export const InputBox = styled.div<{ size?: 'small' | 'normal' }>`
  position: relative;

  & > input {
    ${({ theme, size }) => (size === 'small' ? theme.fonts.button1 : theme.fonts.h2)};
    width: ${({ size }) => (size === 'small' ? '' : '100%')};
    padding: ${({ size }) => (size === 'small' ? '0.1rem' : '0.4rem')};
    text-align: ${({ size }) => (size === 'small' ? 'center' : '')};
    border-bottom: ${({ theme }) => `0.1rem solid ${theme.colors.black}`};
  }
`;

export const ErrorMessage = styled.p`
  ${({ theme }) => theme.fonts.button1};
  position: absolute;
  top: 2.8rem;
  color: ${({ theme }) => theme.colors.red};
`;
