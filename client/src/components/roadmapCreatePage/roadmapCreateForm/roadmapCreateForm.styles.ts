import styled from 'styled-components';

export const Title = styled.h1`
  display: flex;
  ${({ theme }) => theme.fonts.nav_title}
  margin-top: 5rem;

  & p {
    color: ${({ theme }) => theme.colors.main_dark};
  }
`;

export const AddButton = styled.button`
  ${({ theme }) => theme.fonts.description5}
  width: 4rem;
  height: 4rem;

  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme }) => theme.colors.main_middle};
  border-radius: 50%;
`;

export const Form = styled.form`
  padding: 5rem 0 10rem 0;
`;

export const ButtonWrapper = styled.div`
  display: flex;
  justify-content: center;
  width: 100%;
  margin: 5rem 0 0 0;
`;

export const CompleteButton = styled.button`
  ${({ theme }) => theme.fonts.h1}
  width: 50%;
  height: 5rem;

  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme }) => theme.colors.main_middle};
  border-radius: 20px;
`;
