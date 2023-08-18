import styled from 'styled-components';
import { StyledLink } from '@components/signUpPage/SignUpForm.styles';

export const ContentHeader = styled.div`
  ${({ theme }) => theme.fonts.h2};
  display: flex;
  align-items: center;
  margin: 2rem 0 2rem 2rem;

  & > svg {
    margin-left: 1rem !important;
  }
`;

export const TabView = styled.div`
  display: flex;
  flex-direction: column;

  margin-bottom: 1rem;
  padding: 1rem;

  border: 1px solid ${({ theme }) => theme.colors.gray200};
  border-radius: 10px;
`;

export const TabList = styled.div`
  display: flex;
  justify-content: space-around;
  margin-bottom: 1rem;
`;

export const Tab = styled.button<{ isActive: boolean }>`
  padding: 1rem 2rem;
  background-color: ${({ isActive, theme }) =>
    isActive ? theme.colors.gray200 : 'transparent'};
  border: none;
  transition: background-color 0.3s ease;
`;

export const TabContent = styled.div``;

export const RoomHeader = styled.div`
  ${({ theme }) => theme.fonts.h3};
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr;

  padding: 1rem 0;

  font-weight: bold;

  background-color: ${({ theme }) => theme.colors.gray_back};
`;

export const RoomDetails = styled.div<{ isOddRow?: boolean }>`
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr;
  padding: 1rem 0;
  background-color: ${({ isOddRow, theme }) =>
    isOddRow ? theme.colors.gray_back : 'transparent'};

  &:hover {
    background-color: ${({ theme }) => theme.colors.transparent_blue};
  }
`;

export const RoomStatus = styled.span`
  color: ${({ theme }) => theme.colors.primary};
`;

export const GoaRoomLink = styled(StyledLink)``;
