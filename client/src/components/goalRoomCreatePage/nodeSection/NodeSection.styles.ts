import styled from 'styled-components';

export const NodeList = styled.form`
  display: flex;
  flex-direction: column;
  row-gap: 2rem;
`;

export const Node = styled.div`
  display: flex;
  align-items: center;
  border: ${({ theme }) => `0.2rem solid ${theme.colors.gray200}`};
  border-radius: 8px;
`;

export const NodeInfo = styled.div`
  width: 20rem;
  height: 20rem;
  margin-right: 2rem;
  padding: 2rem;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 8px;
`;

export const NodeConfigs = styled.div`
  display: flex;

  & > *:not(:last-child) {
    margin-right: 2rem;
  }
`;

export const DateConfig = styled.div`
  & > div:not(:last-child) {
    margin-bottom: 4rem;
  }
`;
