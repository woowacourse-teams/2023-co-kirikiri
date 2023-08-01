import styled from 'styled-components';

export const Stepper = styled.div`
  overflow: hidden;
  display: flex;
  align-items: center;

  width: 8rem;
  height: fit-content;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 4px;

  & > * {
    flex: 1;
  }
`;

export const Count = styled.div`
  ${({ theme }) => theme.fonts.description2}
  text-align: center;
`;

export const Button = styled.button`
  ${({ theme }) => theme.fonts.button2}
  display: flex;
  align-items: center;
  justify-content: center;

  padding: 0.5rem;

  background-color: inherit;
`;
