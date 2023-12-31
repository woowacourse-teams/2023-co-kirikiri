import styled from 'styled-components';

export const InputField = styled.div`
  box-sizing: content-box;
`;

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
  ${({ theme }) => theme.fonts.nav_text};
  color: ${({ theme }) => theme.colors.gray300};
`;

export const InputBox = styled.div<{
  size?: 'small' | 'normal';
  type?: 'text' | 'date' | 'textarea' | 'number';
}>`
  & > input {
    ${({ theme, size }) => (size === 'small' ? theme.fonts.button1 : theme.fonts.h2)};
    width: ${({ size, type }) =>
      type === 'number' ? '7rem' : size === 'small' ? '' : '100%'};
    padding: ${({ size, type }) =>
      type === 'number' ? '0.4rem' : size === 'small' ? '0.1rem' : '0.4rem'};

    text-align: ${({ size }) => (size === 'small' ? 'center' : '')};

    border: ${({ theme, type }) =>
      type === 'number' ? `0.1rem solid ${theme.colors.black}` : 'none'};
    border-bottom: ${({ theme }) => `0.1rem solid ${theme.colors.black}`};
    border-radius: ${({ type }) => (type === 'number' ? '4px' : '')};
  }

  & > textarea {
    ${({ theme, size }) => (size === 'small' ? theme.fonts.button1 : theme.fonts.h2)};
    width: 100%;
    height: 15rem;
    padding: 1.5rem 1rem;

    border: ${({ theme }) => `3px solid ${theme.colors.main_dark}`};
    border-radius: 8px;
  }
`;

export const ErrorMessage = styled.p<{ size?: 'small' | 'normal' }>`
  ${({ theme }) => theme.fonts.button1};
  position: absolute;
  color: ${({ theme }) => theme.colors.red};
`;
