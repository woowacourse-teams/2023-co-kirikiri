import styled from 'styled-components';

export const BackButtonContainer = styled.button`
  position: absolute;
  top: 1rem;
  left: 1rem;

  display: flex;
  align-items: center;
  justify-content: center;

  width: 3.5rem;
  height: 3rem;

  color: ${({ theme }) => theme.colors.white};

  background: ${({ theme }) => theme.colors.main_dark};
  border: 1px solid ${({ theme }) => theme.colors.gray300};

  transition: transform 0.2s ease-in-out;

  &:hover {
    transform: scale(1.03);
  }
`;
