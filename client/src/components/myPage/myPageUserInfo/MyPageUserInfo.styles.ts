import styled from 'styled-components';

export const UserInfoWrapper = styled.section`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 100%;
  padding: 4rem;
`;

export const UserDetails = styled.figure`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
`;

export const UserInfoImageContainer = styled.figure`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 15rem;
  height: 15rem;

  border-radius: 50%;
`;

export const UserInfoImage = styled.img`
  width: 100%;
  height: 100%;
  margin-bottom: 2rem;
  border-radius: 50%;
`;

export const UserNickname = styled.figcaption`
  ${({ theme }) => theme.fonts.h2};
  display: flex;
  align-items: center;
  justify-content: center;
`;

export const UserIdentifier = styled.span`
  ${({ theme }) => theme.fonts.description1};
  text-align: center;
`;
