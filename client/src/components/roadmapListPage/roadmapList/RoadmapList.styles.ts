import styled from 'styled-components';

export const RoadmapList = styled.div`
  display: flex;
  flex-wrap: wrap;
  row-gap: 3rem;
  justify-content: space-around;

  margin-bottom: 8rem;
`;

export const CreateRoadmapButton = styled.button`
  ${({ theme }) => theme.fonts.h1}
  position: fixed;
  top: 1rem;

  width: 50%;
  height: 5rem;

  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme }) => theme.colors.main_middle};
  border-radius: 20px;
`;
