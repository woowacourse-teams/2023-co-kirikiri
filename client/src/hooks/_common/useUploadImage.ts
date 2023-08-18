import { ChangeEvent, useState } from 'react';

export const useUploadImage = () => {
  const [imagePreviews, setImagePreviews] = useState<string[]>([]);
  const [imageFile, setImageFile] = useState<File[]>([]);

  const getFileFromElement = (event: ChangeEvent<HTMLInputElement>) => {
    const { files } = event.target;

    if (files === null) throw Error('no file!');

    return files;
  };

  const showPrevImage = (uploadedFile: File) => {
    const reader = new FileReader();

    setImageFile((prev) => [...prev, uploadedFile]);
    reader.onloadend = () => {
      setImagePreviews((prev) => [...prev, reader.result as string]);
    };

    reader.readAsDataURL(uploadedFile);
  };

  const checkFileSize = (maxSize: number, file: File) => {
    if (file.size > maxSize) {
      alert('사진의 용량은 3MB를 넘을 수 없습니다.');
      return false;
    }
    return true;
  };

  return {
    imagePreviews,
    imageFile,
    getFileFromElement,
    showPrevImage,
    checkFileSize,
  };
};
