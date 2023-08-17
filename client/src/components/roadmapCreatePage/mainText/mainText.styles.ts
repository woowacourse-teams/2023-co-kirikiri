import styled from 'styled-components';

export const Container = styled.article`
  margin-top: 6rem;
`;

export const FieldWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;

  width: 80%;
  height: 30rem;
  padding-left: 2rem;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 2rem;
`;

export const TextCountWrapper = styled.div`
  height: 80%;
  margin-right: 2rem;
`;
