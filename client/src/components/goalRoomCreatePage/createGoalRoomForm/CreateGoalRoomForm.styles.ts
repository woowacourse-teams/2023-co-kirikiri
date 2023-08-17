import { styled } from 'styled-components';

export const Form = styled.form`
  margin: 6rem 0;
`;

export const SubmitButtonWrapper = styled.div`
  display: flex;
  justify-content: end;
  margin-top: 3rem;

  & > button {
    padding: 1.8rem 3rem;
    color: ${({ theme }) => theme.colors.white};
    background-color: ${({ theme }) => theme.colors.main_dark};
    border-radius: 8px;
  }
`;
