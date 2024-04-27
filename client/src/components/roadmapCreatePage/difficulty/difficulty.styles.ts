import styled from 'styled-components';

export const DifficultyLabel = styled.h2`
  ${({ theme }) => theme.fonts.title_large}
  display: flex;
  margin-top: 2rem;
  margin-bottom: 1rem;
  color: ${({ theme }) => theme.colors.black};

  > p {
    color: red;
  }
`;

export const DifficultyDescription = styled.p`
  ${({ theme }) => theme.fonts.description4}
  margin-bottom: 2.5rem;
  color: ${({ theme }) => theme.colors.gray300};
`;

export const Wrapper = styled.ul`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: space-around;

  width: 15.4rem;
  height: 21.9rem;
  margin-top: 1.9rem;
  padding-left: 2rem;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 0.5rem;
`;

export const TriggerButton = styled.button`
  cursor: pointer;

  display: flex;
  align-items: center;
  justify-content: center;

  width: 15.4rem;
  height: 4rem;

  background-color: transparent;
  border: 0.2rem solid ${({ theme }) => theme.colors.gray300};
  border-radius: 1rem;
`;

export const DifficultyValue = styled.div`
  ${({ theme }) => theme.fonts.description5}
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const DifficultyOption = styled.li<{ isSelected?: boolean }>`
  ${({ theme }) => theme.fonts.button2}
  display: flex;
  align-items: center;

  color: ${({ theme, isSelected }) =>
    isSelected !== undefined &&
    (isSelected ? theme.colors.main_dark : theme.colors.gray300)};

  cursor: pointer;

  width: 100%;
`;

export const OptionIndicator = styled.span<{ isSelected?: boolean }>`
  width: 1rem;
  height: 1rem;
  margin-right: 1.8rem;

  background-color: ${({ theme, isSelected }) =>
    isSelected !== undefined && (isSelected ? theme.colors.main_dark : 'transparent')};
  border: 0.1rem solid
    ${({ theme, isSelected }) =>
      isSelected !== undefined &&
      (isSelected ? theme.colors.main_dark : theme.colors.gray300)};
`;
