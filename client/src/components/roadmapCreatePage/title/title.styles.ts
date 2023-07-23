import styled from 'styled-components';

export const Container = styled.article`
  margin-top: 3rem;
`;

export const FieldWrapper = styled.div`
  display: flex;
  justify-content: space-between;

  width: 60%;
  height: 3rem;
  padding-left: 2rem;

  border-bottom: 0.2rem solid ${({ theme }) => theme.colors.gray300};
`;

export const ErrorMessage = styled.div`
  height: 2rem;
  margin-top: 1rem;
  color: ${({ theme }) => theme.colors.red};
`;
