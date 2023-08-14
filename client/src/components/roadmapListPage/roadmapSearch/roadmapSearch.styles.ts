import styled from 'styled-components';

export const Wrapper = styled.section`
  display: flex;
  justify-content: space-around;

  width: 57.6rem;
  height: 3.6rem;
  margin: 0 3rem;
  margin-bottom: 3rem;

  border-bottom: 0.2rem solid ${({ theme }) => theme.colors.gray300};
`;

export const InputWrapper = styled.div`
  display: inline;
  width: 40rem;
`;

export const SearchInput = styled.input`
  ${({ theme }) => theme.fonts.description5}
  float: inline-end;
  width: 100%;
`;

export const SearchButton = styled.button``;
