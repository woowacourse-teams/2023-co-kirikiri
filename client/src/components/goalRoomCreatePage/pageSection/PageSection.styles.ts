import styled from 'styled-components';

export const PageSection = styled.div`
  margin-top: 6rem;
`;

export const SectionTitle = styled.div<{ isRequired: boolean }>`
  ${({ theme }) => theme.fonts.nav_title};

  ${({ isRequired }) =>
    isRequired &&
    `&::after {
    content: '*';
    color: red;
  }`};
`;

export const Description = styled.div`
  ${({ theme }) => theme.fonts.nav_text};
  color: ${({ theme }) => theme.colors.gray300};
`;

export const ChildrenWrapper = styled.div`
  margin-top: 1.8rem;
`;
