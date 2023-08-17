import { styled } from 'styled-components';

export const RoadmapNodeList = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-around;

  width: 100%;
  min-height: 30rem;
  margin-top: 3rem;
  padding-top: 2rem;

  border-radius: 12px;
  box-shadow: ${({ theme }) => theme.shadows.box};
`;

export const Title = styled.h1`
  ${({ theme }) => theme.fonts.nav_title}
  color: ${({ theme }) => theme.colors.gray300};

  & > strong {
    color: ${({ theme }) => theme.colors.main_dark};
  }
`;

export const NodeItemContainer = styled.article`
  display: flex;
  align-items: center;
  justify-content: space-around;
  margin-top: 2rem;
`;

export const NodeIndicator = styled.div`
  ${({ theme }) => theme.fonts.nav_title}
  width: 4rem;
  height: 4rem;

  background-color: ${({ theme }) => theme.colors.main_dark};
  border-radius: 50%;

  display: flex;
  justify-content: center;
  align-items: center;

  color: ${({ theme }) => theme.colors.white};
`;

export const NodeContent = styled.div`
  overflow-y: scroll;
  display: flex;
  flex-direction: column;
  align-items: center;

  width: 50rem;
  height: 15rem;
  padding: 2rem 0;

  border: 0.3rem solid ${({ theme }) => theme.colors.main_dark};
  border-left: none;
  border-radius: 30px;
`;

export const NodeTitle = styled.h2`
  ${({ theme }) => theme.fonts.h2}
  color: ${({ theme }) => theme.colors.black};
`;

export const NodeDescription = styled.div`
  ${({ theme }) => theme.fonts.description5}
  width: 40rem;
  color: ${({ theme }) => theme.colors.gray300};
  text-align: center;
`;

export const ImageWrapper = styled.div`
  display: flex;
`;

export const NodeImage = styled.img`
  width: 15rem;
  height: 20rem;
  object-fit: cover;
`;
