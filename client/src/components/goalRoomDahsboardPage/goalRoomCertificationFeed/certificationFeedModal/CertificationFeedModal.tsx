import * as S from './CertificationModal.styles';
import { ChangeEvent, useState } from 'react';
import InputField from '@components/roadmapCreatePage/input/inputField/InputField';
import { useValidateInput } from '@hooks/_common/useValidateInput';
import { CERTIFICATION_FEED } from '@constants/goalRoom/regex';
import TextCount from '@components/roadmapCreatePage/input/textCount/TextCount';
import { useCreateCertificationFeed } from '@hooks/queries/goalRoom';
import { useGoalRoomDashboardContext } from '@/context/goalRoomDashboardContext';

const CertificationFeedModal = () => {
  const { goalroomId } = useGoalRoomDashboardContext();

  const [imagePreview, setImagePreview] = useState<string | null>('');
  const [imageFile, setImageFile] = useState<File | null>(null); // add this state for file
  const { handleInputChange, validateInput, errorMessage, resetErrorMessage, value } =
    useValidateInput(CERTIFICATION_FEED);

  const { createCertificationFeed } = useCreateCertificationFeed(goalroomId);

  const handleImageChange = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files ? event.target.files[0] : null;

    if (file) {
      setImageFile(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleRemoveImage = () => {
    setImagePreview(null);
    setImageFile(null);
  };

  const handleFormSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    if (!imageFile || !value) return;

    const formData = new FormData();

    formData.append('image', imageFile);
    formData.append('description', value);

    createCertificationFeed(formData);
  };

  return (
    <S.CertificationModalWrapper>
      <S.CertificationHeader>인증피드</S.CertificationHeader>
      <S.CertificationText>새로운 인증피드 등록</S.CertificationText>
      {imagePreview && (
        <S.PreviewWrapper>
          <S.PreviewImage src={imagePreview} alt='업로드한 인증 피드 이미지' />
          <S.PreviewDeleteButton onClick={handleRemoveImage}>X</S.PreviewDeleteButton>
        </S.PreviewWrapper>
      )}

      <form action='' onSubmit={handleFormSubmit}>
        {!imagePreview && (
          <S.FileUploadCard htmlFor='fileInput'>
            <S.PlusButton>인증피드 사진 업로드</S.PlusButton>
            <input
              id='fileInput'
              type='file'
              onChange={handleImageChange}
              style={{ display: 'none' }}
            />
          </S.FileUploadCard>
        )}
        <S.InputFieldWrapper>
          <InputField
            placeholder='컨텐츠를 소개하는 문장을 작성해주세요'
            handleInputChange={handleInputChange}
            maxLength={250}
            validateInput={validateInput}
            resetErrorMessage={resetErrorMessage}
            name='introduction'
            data-valid={validateInput}
          />
        </S.InputFieldWrapper>
        <TextCount maxCount={250} currentCount={value.length} />
        <S.ErrorMessage>{errorMessage}</S.ErrorMessage>
        <S.CertificationSubmitButton type='submit'>
          인증피드 등록
        </S.CertificationSubmitButton>
      </form>
    </S.CertificationModalWrapper>
  );
};

export default CertificationFeedModal;
