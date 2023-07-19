import styled from 'styled-components';
import media from "@styles/media";

export const SingleCardWrapper = styled.div`
  position: relative;

  width: 70%;
  min-height: 60vh;
  padding: 1.5rem;

  border: 1px solid ${({ theme }) => theme.colors.gray300};
  border-radius: 20px;
  box-shadow: ${({ theme }) => theme.shadows.box};

  ${media.mobile`
    min-height: 50vh;
  `}
`;
