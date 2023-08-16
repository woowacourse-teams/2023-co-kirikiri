import * as S from './CertificationModal.styles';
import { ChangeEvent, useState } from 'react';
import InputField from '@components/roadmapCreatePage/input/inputField/InputField';
import { useValidateInput } from '@hooks/_common/useValidateInput';
import { CERTIFICATION_FEED } from '@constants/goalRoom/regex';
import TextCount from '@components/roadmapCreatePage/input/textCount/TextCount';
import {
  useCertificationFeeds,
  useCreateCertificationFeed,
} from '@hooks/queries/goalRoom';
import { useGoalRoomDashboardContext } from '@/context/goalRoomDashboardContext';

const CertificationFeedModal = () => {
  const { goalroomId } = useGoalRoomDashboardContext();
  const { certificationFeeds } = useCertificationFeeds(goalroomId);

  const [imagePreview, setImagePreview] = useState<string | null>('');
  const [imageFile, setImageFile] = useState<File | null>(null);
  const {
    handleInputChange,
    validateInput,
    errorMessage,
    resetErrorMessage,
    value,
    resetValue,
  } = useValidateInput(CERTIFICATION_FEED);

  const resetCertificationFeedInputs = () => {
    setImagePreview(null);
    setImageFile(null);
    resetValue();
  };

  const { createCertificationFeed } = useCreateCertificationFeed(
    goalroomId,
    resetCertificationFeedInputs
  );

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

      <S.CertificationFeedsWrapper>
        <form onSubmit={handleFormSubmit}>
          {imagePreview && (
            <S.PreviewWrapper>
              <S.PreviewImage src={imagePreview} alt='업로드한 인증 피드 이미지' />
              <S.PreviewDeleteButton onClick={handleRemoveImage}>X</S.PreviewDeleteButton>
            </S.PreviewWrapper>
          )}
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

        {certificationFeeds.map((feed) => {
          return (
            <S.CertificationFeedCard key={feed.checkFeed.id}>
              <S.CertificationFeedImage
                src={feed.checkFeed.imageUrl}
                alt='인증피드 이미지'
              />
              <S.CertificationFeedDescription>
                {feed.checkFeed.description}
              </S.CertificationFeedDescription>
              <S.CertificationFeedsUserInfo>
                <S.CertificationFeedsUserImage
                  src={feed.member.imageUrl}
                  alt='유저 이미지'
                />
                <S.CertificationFeedsUserName>
                  {feed.member.nickname}
                </S.CertificationFeedsUserName>
              </S.CertificationFeedsUserInfo>
              <S.CreatedAtText>{feed.checkFeed.createdAt}</S.CreatedAtText>
            </S.CertificationFeedCard>
          );
        })}
      </S.CertificationFeedsWrapper>
    </S.CertificationModalWrapper>
  );
};

export default CertificationFeedModal;
