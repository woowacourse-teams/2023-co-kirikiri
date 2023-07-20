import { styled } from 'styled-components';

export const Box = styled.div`
  ${({ theme }) => theme.fonts.description4}
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  width: 8rem;
  height: 8rem;

  background-color: ${({ theme }) => theme.colors.white};
  border-radius: 2rem;
`;
