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

  min-height: 6rem;
  padding: 0 1rem;

  input,
  select {
    pointer-events: auto;
    width: 100%;
    margin-left: 1rem;
    border: none;
  }

  &:not(:last-child) {
    border-bottom: 1px solid ${({ theme }) => theme.colors.gray300};
  }
`;

export const InfoText = styled.p<{ centered?: boolean }>`
  ${({ theme }) => theme.fonts.description4};
  margin-top: 1rem;
  text-align: ${({ centered }) => (centered ? 'center' : 'left')};
`;

export const BoldText = styled.span`
  font-weight: 700;
`;

export const SubmitButton = styled.button`
  ${({ theme }) => theme.fonts.normal_bold};
  display: flex;
  align-items: center;
  justify-content: center;

  width: 100%;
  height: 6rem;
  margin-top: 1.5rem;
  padding: 2rem;

  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme }) => theme.colors.main_dark};
  border: none;
  border-radius: 6px;
`;

export const StyledLink = styled(Link)`
  ${({ theme }) => theme.fonts.small_bold};
  color: ${({ theme }) => theme.colors.main_dark};

  &:hover {
    text-decoration: underline;
  }
`;

export const ErrorBox = styled.div`
  ${({ theme }) => theme.fonts.description4}
  padding: 1rem 1rem;
  height: 14rem;
  color: ${({ theme }) => theme.colors.red};
  p {
    &:not(:last-child) {
      margin-bottom: 0.6rem;
    }
  }
`;

export const FormWrapper = styled.form`
  width: 45rem;
`;

export const Logo = styled.img`
  height: 4rem;
`;
