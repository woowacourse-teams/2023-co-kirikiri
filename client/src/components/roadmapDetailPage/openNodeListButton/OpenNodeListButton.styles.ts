import { styled } from 'styled-components';

export const OpenNodeListButton = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  width: 40rem;
  height: 30rem;
  margin-top: 3rem;
  padding: 3rem 3rem;

  border-radius: 12px;
  box-shadow: ${({ theme }) => theme.shadows.box};
`;
