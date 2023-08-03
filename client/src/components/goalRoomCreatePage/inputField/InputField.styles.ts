import { styled } from 'styled-components';
import { InputType } from './InputField';

export const InputField = styled.label`
  display: block;
  margin-bottom: 6rem;
`;

export const Label = styled.div<{ isRequired: boolean; type: InputType }>`
  ${({ theme, type }) =>
    type === 'normal' ? theme.fonts.nav_title : theme.fonts.description2};

  ${({ isRequired }) =>
    isRequired &&
    `&::after {
  content: '*';
  color: red;
}`};
`;

export const Description = styled.div`
  ${({ theme }) => theme.fonts.nav_text};
  color: ${({ theme }) => theme.colors.gray300};
`;

export const ChildrenWrapper = styled.div<{ type: InputType }>`
  margin-top: ${({ type }) => (type === 'normal' ? '1.8rem' : '1.2rem')};
`;
