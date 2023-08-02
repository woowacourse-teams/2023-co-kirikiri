import media from '@/styles/media';
import { styled } from 'styled-components';

export const Form = styled.form``;

export const RoadmapInfo = styled.div`
  ${({ theme }) => theme.fonts.description4}
`;

export const InputField = styled.label`
  width: 80%;
  height: 80%;
  color: ${({ theme }) => theme.colors.gray300};
  &::placeholder {
    ${({ theme }) => theme.fonts.description4};
    color: ${({ theme }) => theme.colors.gray200};
  }
`;

export const Input = styled.input`
  ${({ theme }) => theme.fonts.nav_title}
  width: 70%;
  padding: 1rem 0.5rem;
  color: ${({ theme }) => theme.colors.gray300};
  border-bottom: ${({ theme }) => `0.1rem solid ${theme.colors.black}`};
  &::placeholder {
    ${({ theme }) => theme.fonts.description4};
    color: ${({ theme }) => theme.colors.gray200};
  }

  ${media.mobile`
    width:100%;
  `}
`;

export const NodeSectionWrapper = styled.div`
  display: flex;
  column-gap: 2rem;
`;

export const NodeList = styled.form<{ nodeCount: number }>`
  display: grid;
  grid-template-rows: ${({ nodeCount }) => `repeat(${nodeCount}, 1fr)`};
  row-gap: 2.4rem;
`;

export const NodeWrapper = styled.div`
  display: flex;
  align-items: center;
`;

export const NodeInfo = styled.div`
  width: 18rem;
  height: 18rem;
  margin-right: 2rem;
  padding: 2rem;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 8px;
`;

export const NodeConfigs = styled.div`
  display: flex;

  & > *:not(:last-child) {
    margin-right: 2rem;
  }
`;

export const DateInput = styled.input`
  padding: 0.7rem 0;
  text-align: center;
  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 4px;
`;

export const Textarea = styled.textarea`
  width: 70%;
  height: 16rem;
  padding: 1rem 0.5rem;

  border: ${({ theme }) => `0.1rem solid ${theme.colors.black}`};
  border-radius: 8px;

  ${media.mobile`
    width:100%;
  `}
`;
