import styled from 'styled-components';
import { Link } from 'react-router-dom';

export const FormList = styled.div`
  width: 100%;
  margin-top: 2rem;
  border: 1px solid ${({ theme }) => theme.colors.gray300};
  border-radius: 6px;
`;

export const FormItem = styled.div`
  pointer-events: none;

  display: flex;
  align-items: center;

  min-height: 5rem;
  padding: 0 1rem;

  border: 1px solid ${({ theme }) => theme.colors.gray300};

  input,
  select {
    pointer-events: auto;
    width: 100%;
    margin-left: 1rem;
    border: none;
  }
`;

export const InfoText = styled.p<{ centered?: boolean }>`
  ${({ theme }) => theme.fonts.description2};
  margin-top: 1rem;
  text-align: ${({ centered }) => (centered ? 'center' : 'left')};
`;

export const BoldText = styled.span`
  font-weight: 700;
`;

export const SubmitButton = styled.button`
  width: 100%;
  height: 3rem;
  margin-top: 1rem;
  padding: 1rem;

  ${({ theme }) => theme.fonts.button1};
  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme }) => theme.colors.main_dark};
  border: none;
  border-radius: 6px;
`;

export const StyledLink = styled(Link)`
  color: ${({ theme }) => theme.colors.main_dark};

  &:hover {
    text-decoration: underline;
  }
`;

export const ErrorBox = styled.div`
  ${({ theme }) => theme.fonts.description4}
  padding: 1rem 1rem;
  color: ${({ theme }) => theme.colors.red};
  p {
    &:not(:last-child) {
      margin-bottom: 0.6rem;
    }
  }
`;
