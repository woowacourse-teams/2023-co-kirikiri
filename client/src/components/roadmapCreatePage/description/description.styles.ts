import styled from 'styled-components';

export const Container = styled.article`
  margin-top: 3rem;
`;

export const FieldWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;

  width: 80%;
  height: 10rem;
  padding: 2rem;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 2rem;
`;

export const TextCountWrapper = styled.div`
  height: 80%;
  margin-right: 2rem;
`;

export const ErrorMessage = styled.div`
  height: 2rem;
  margin-top: 1rem;
  color: ${({ theme }) => theme.colors.red};
`;
