import styled from 'styled-components';

const DifficultyLabel = styled.h2`
  display: flex;
  margin-bottom: 1rem;
  ${({ theme }) => theme.fonts.title_large}
  color: ${({ theme }) => theme.colors.black};

  > p {
    color: red;
  }
`;

const DifficultyDescription = styled.p`
  margin-bottom: 2.5rem;
  ${({ theme }) => theme.fonts.description5}
  color: ${({ theme }) => theme.colors.gray300};
`;

const Wrapper = styled.ul`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: space-around;

  width: 15.4rem;
  height: 21.9rem;
  margin-top: 1.9rem;
  padding-left: 2rem;

  background-color: ${({ theme }) => theme.colors.gray200};
  border-radius: 0.5rem;
`;

const TriggerButton = styled.button`
  width: 15.4rem;
  height: 4rem;

  background-color: transparent;
  border: 0.2rem solid ${({ theme }) => theme.colors.gray300};
  border-radius: 1rem;
`;

const DifficultyOption = styled.li<{ isSelected?: boolean }>`
  display: flex;
  ${({ theme }) => theme.fonts.button3}
  color: ${({ theme, isSelected }) =>
    isSelected !== undefined &&
    (isSelected ? theme.colors.main_dark : theme.colors.gray300)};
  cursor: pointer;
`;

const OptionIndicator = styled.span<{ isSelected?: boolean }>`
  width: 1rem;
  height: 1rem;
  margin-right: 1.8rem;

  background-color: ${({ theme, isSelected }) =>
    isSelected !== undefined && (isSelected ? theme.colors.main_dark : 'transparent')};
  border: 0.1rem solid
    ${({ theme, isSelected }) =>
      isSelected !== undefined && (isSelected ? 'none' : theme.colors.main_dark)};
`;

export const S = {
  DifficultyLabel,
  DifficultyDescription,
  Wrapper,
  TriggerButton,
  DifficultyOption,
  OptionIndicator,
};
