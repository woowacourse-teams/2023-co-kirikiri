import styled, { keyframes } from 'styled-components';

const shake = keyframes`
  10%, 90% {
    transform: translate3d(-0.5px, 0, 0) rotate(-0.5deg);
  }

  20%, 80% {
    transform: translate3d(1px, 0, 0) rotate(0.5deg);
  }

  30%, 50%, 70% {
    transform: translate3d(-0.5px, 0, 0) rotate(-0.5deg);
  }

  40%, 60% {
    transform: translate3d(1px, 0, 0) rotate(0.5deg);
  }
`;

export const Todo = styled.li`
  display: flex;
  align-items: center;
  justify-content: space-between;

  margin-bottom: 1rem;
  padding: 1rem;

  list-style: none;

  border: 1px solid #ccc;
  border-radius: 5px;
  box-shadow: ${({ theme }) => theme.shadows.main};

  transition: all 0.5s ease;

  &:hover {
    animation: ${shake} 1s;
  }
`;

export const TodoButton = styled.button`
  cursor: pointer;
  padding: 0;
  background-color: transparent;
  border: none;
`;

export const TodoContent = styled.span`
  ${({ theme }) => theme.fonts.button1};
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
`;

export const TodoDate = styled.div`
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  width: 6rem;
`;

export const TodoDateSpan = styled.span`
  font-size: 0.9rem;
  color: #888;
`;
