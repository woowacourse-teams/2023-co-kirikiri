import { styled } from 'styled-components';

export const RoadmapListView = styled.div`
  display: flex;
  flex-direction: column;
  row-gap: 2.5rem;
  margin-top: 8rem;
`;

export const ServiceDescWrapper = styled.div`
  display: flex;
  justify-content: center;
  width: 100%;
  margin: 2rem 0 2rem 0;
`;

export const ServiceDescContent = styled.div`
  width: 70rem;
  padding: 1rem;
  background: ${({ theme }) => theme.colors.gray100};
  border-radius: 10px;
`;

export const ServiceDesc = styled.p`
  ${({ theme }) => theme.fonts.description3};
  text-align: center;
  line-height: 2;
`;
