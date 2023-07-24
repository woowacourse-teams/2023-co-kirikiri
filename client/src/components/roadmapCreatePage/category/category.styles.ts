import styled from 'styled-components';

const CategoryLabel = styled.h2`
  display: flex;
  margin-bottom: 1rem;
  ${({ theme }) => theme.fonts.title_large}
  color: ${({ theme }) => theme.colors.black};

  > p {
    color: red;
  }
`;

const CategoryDescription = styled.p`
  margin-bottom: 2.5rem;
  ${({ theme }) => theme.fonts.description5}
  color: ${({ theme }) => theme.colors.gray300};
`;

const Wrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  width: 85.8rem;
  height: 10.5rem;
`;

const CategoryBox = styled.article<{ isSelected?: boolean }>`
  cursor: pointer;

  display: flex;
  align-items: center;
  justify-content: center;

  width: 12.8rem;
  height: 4.7rem;
  margin-right: 1.8rem;

  color: ${({ theme, isSelected }) =>
    isSelected !== undefined && (isSelected ? theme.colors.white : theme.colors.gray300)};

  background-color: ${({ theme, isSelected }) =>
    isSelected !== undefined &&
    (isSelected ? theme.colors.main_light : theme.colors.gray100)};
  border-radius: 40px;

  ${({ theme }) => theme.fonts.title_large}
`;

export const S = {
  CategoryLabel,
  CategoryDescription,
  Wrapper,
  CategoryBox,
};
