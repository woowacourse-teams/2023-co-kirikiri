import styled from 'styled-components';

export const BackDrop = styled.div`
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;

  background-color: rgb(0, 0, 0, 0.5);
`;

export const Container = styled.section`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);

  overflow-y: scroll;
  display: flex;
  flex-direction: column;
  align-items: center;

  width: 55rem;
  height: 55rem;
  padding: 3.5rem 0;

  background-color: ${({ theme }) => theme.colors.white};
  border-radius: 30px;
`;

export const CloseButton = styled.button`
  ${({ theme }) => theme.fonts.h1}
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const TitleWrapper = styled.div`
  display: flex;
  justify-content: space-around;
  width: 100%;
  padding: 0 1rem;
`;

export const Title = styled.h1`
  ${({ theme }) => theme.fonts.h1}
`;

export const Participant = styled.h2`
  ${({ theme }) => theme.fonts.h2}
  display: flex;
  margin-top: 0.7rem;

  & p {
    color: ${({ theme }) => theme.colors.main_dark};
  }
`;

export const RoadmapContainer = styled.ul`
  overflow: scroll;
  display: flex;
  flex-direction: column;
  gap: 4rem;
  align-items: center;

  width: 41rem;
  min-height: 50rem;
  margin-top: 3.3rem;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 20px;
`;

export const RoadmapTitle = styled.h3`
  ${({ theme }) => theme.fonts.button1}
  margin-top: 3.3rem;
`;

export const NodeContainer = styled.li`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-around;

  width: 27.5rem;
  height: 10rem;
  padding: 1.2rem 0;

  border: 0.2rem solid ${({ theme }) => theme.colors.main_dark};
  border-radius: 20px;
`;

export const NodeTitle = styled.div`
  ${({ theme }) => theme.fonts.button1}
`;

export const NodePeriod = styled.div`
  ${({ theme }) => theme.fonts.button1}
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const FeedCount = styled.div`
  ${({ theme }) => theme.fonts.description4}
  color: ${({ theme }) => theme.colors.gray300};
`;

export const EnterGoalRoomButton = styled.button`
  ${({ theme }) => theme.fonts.h1}
  position: absolute;
  bottom: 4rem;
  left: 50%;
  transform: translate(-50%);

  width: 39.4rem;
  height: 7rem;

  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme }) => theme.colors.main_dark};
  border-radius: 34px;
`;
