import styled, { css } from 'styled-components';
import media from '@styles/media';

export const SingleCardWrapper = styled.div<{
  noneMobileBorder?: boolean;
}>`
  position: relative;

  display: flex;
  flex-direction: column;
  align-items: center;

  width: 50rem;
  padding: 4rem 1.5rem;

  border: 1px solid ${({ theme }) => theme.colors.gray300};
  border-radius: 20px;
  box-shadow: ${({ theme }) => theme.shadows.box};

  ${({ noneMobileBorder: simpleMobile }) =>
    media.mobile(css`
      width: 100%;
      border: ${simpleMobile && 'none'};
      box-shadow: none;
    `)}
`;
