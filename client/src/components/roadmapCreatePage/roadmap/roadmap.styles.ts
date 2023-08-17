import styled from 'styled-components';

export const TitleWrapper = styled.div`
  display: flex;
`;

export const RoadmapNumber = styled.div`
  ${({ theme }) => theme.fonts.description5}
  display: flex;
  justify-content: center;
  align-items: center;

  width: 3rem;
  height: 3rem;

  border: 0.2rem solid ${({ theme }) => theme.colors.black};
  border-radius: 5px;

  margin-right: 1rem;
`;

export const TitleFieldWrapper = styled.div`
  display: flex;
  justify-content: space-between;

  width: 60%;
  height: 3rem;
  padding-left: 2rem;

  border-bottom: 0.2rem solid ${({ theme }) => theme.colors.gray300};
`;

export const BodyFieldWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;

  width: 70%;
  height: 15rem;
  margin-top: 2rem;
  margin-bottom: 3rem;
  padding-left: 2rem;

  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 2rem;
`;

export const NodeTitleInputField = styled.input`
  width: 80%;
`;

export const NodeBodyInputField = styled.textarea`
  width: 80%;
  height: 10rem;
`;

export const AddButton = styled.button`
  width: 13rem;
  height: 4rem;
  border: 0.1rem solid ${({ theme }) => theme.colors.main_dark};
  border-radius: 15px;
`;

export const ImageSection = styled.section`
  position: relative;

  display: flex;
  align-items: center;
  justify-content: center;

  width: 30%;
  height: 100%;
  padding-right: 4rem;
`;

export const NodeInputLabel = styled.label`
  position: absolute;
  right: 2rem;
  bottom: 2rem;
`;

export const NodeFileInput = styled.input`
  display: none;
`;

export const UploadedNodeImage = styled.img`
  width: 6rem;
  height: 9rem;
  margin-right: 1rem;
  object-fit: cover;
`;
