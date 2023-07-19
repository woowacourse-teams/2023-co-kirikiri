import styled from 'styled-components';

export const Container = styled.article`
  margin-top: 6rem;
`;

export const FieldWrapper = styled.div`
  display: flex;
  justify-content: space-between;

  width: 50%;
  height: 2rem;
  padding-left: 2rem;

  border-bottom: 0.2rem solid ${({ theme }) => theme.colors.gray300};
`;

export const ErrorMessage = styled.div`
  height: 2rem;
  color: ${({ theme }) => theme.colors.red};
`;
