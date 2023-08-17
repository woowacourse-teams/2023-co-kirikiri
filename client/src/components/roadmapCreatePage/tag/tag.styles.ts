import styled from 'styled-components';

export const Container = styled.section`
  margin-top: 5.5rem;
`;

export const TagWrapper = styled.div`
  ${({ theme }) => theme.fonts.description5};
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  color: ${({ theme }) => theme.colors.black};
`;

export const AddedTagItem = styled.article`
  display: flex;
  align-items: center;
  justify-content: center;

  height: 4.7rem;
  padding: 0 1rem;

  border: 0.2rem solid ${({ theme }) => theme.colors.main_dark};
  border-radius: 100px;
`;

export const TagItem = styled.article<{ width: number }>`
  display: flex;
  align-items: center;
  justify-content: center;

  width: ${({ width }) => (width > 4 ? width * 2 : 10)}rem;
  height: 4.7rem;

  border: 0.2rem solid ${({ theme }) => theme.colors.main_dark};
  border-radius: 100px;
`;

export const TagInputField = styled.input`
  ${({ theme }) => theme.fonts.description5};
  width: 80%;
  text-align: center;
`;

export const AddButton = styled.button`
  ${({ theme }) => theme.fonts.nav_title}
  width: 5rem;
  height: 5rem;
`;

export const DeleteButton = styled.button`
  margin-right: 2rem;
  color: ${({ theme }) => theme.colors.main_dark};
`;
