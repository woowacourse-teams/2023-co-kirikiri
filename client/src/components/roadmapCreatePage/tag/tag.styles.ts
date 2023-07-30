import styled from 'styled-components';

export const Container = styled.section`
  margin-top: 5.5rem;
`;

export const TagWrapper = styled.div`
  ${({ theme }) => theme.fonts.nav_title};
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const TagItem = styled.article<{ width: number }>`
  display: flex;
  align-items: center;
  justify-content: center;

  width: ${({ width }) => (width > 4 ? width * 2 : 10)}rem;
  height: 4.7rem;
  margin-right: 2rem;

  border: 0.2rem solid ${({ theme }) => theme.colors.main_dark};
  border-radius: 100px;
`;

export const TagInputField = styled.input`
  width: 80%;
  text-align: center;
`;

export const AddButton = styled.button`
  /* ${({ theme }) => theme.fonts.nav_title} */
  width: 5rem;
  height: 5rem;
`;
