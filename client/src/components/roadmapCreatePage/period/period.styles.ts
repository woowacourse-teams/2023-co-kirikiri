import styled from 'styled-components';

export const Container = styled.article`
  margin-top: 6rem;
`;

export const FieldWrapper = styled.div`
  display: flex;
  justify-content: space-between;

  width: 7rem;
  height: 2.5rem;
  padding-left: 1rem;

  border-bottom: 0.2rem solid ${({ theme }) => theme.colors.gray300};
  ${({ theme }) => theme.fonts.button2}

  > p {
    padding-bottom: 1rem;
  }
`;

export const ErrorMessage = styled.div`
  height: 2rem;
  margin-top: 1rem;
  color: ${({ theme }) => theme.colors.red};
`;
