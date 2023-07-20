import styled from 'styled-components';

const DefaultLabel = styled.div`
  ${({ theme }) => theme.fonts.title_large}
`;

const DefaultDescription = styled.div`
  ${({ theme }) => theme.fonts.description5}
`;

const DefaultTrigger = styled.div`
  width: 2rem;
  height: 2rem;
`;

const DefaultIndicator = styled.div<{ isSelected: boolean }>`
  width: 0.2rem;
  height: 0.2rem;
`;

const DefaultOptionGroup = styled.div`
  width: 2rem;
`;

const DefaultOption = styled.div<{ isSelected: boolean }>`
  width: 8rem;
  height: 2rem;
`;

export const S = {
  DefaultLabel,
  DefaultDescription,
  DefaultTrigger,
  DefaultIndicator,
  DefaultOptionGroup,
  DefaultOption,
};
