import { useValidateInput } from '@hooks/_common/useValidateInput';
import {
  BIRTHDAY,
  IDENTIFIER,
  NICKNAME,
  PASSWORD,
  PHONE_NUMBER,
  GENDER,
} from '@constants/user/regex';
import { useEffect, useState } from 'react';

import { MemberJoinRequest } from '@myTypes/user/remote';
import { getInvariantObjectKeys, invariantOf } from '@utils/_common/invariantType';

export const useSignUpFormValidation = () => {
  const identifierValidation = useValidateInput(IDENTIFIER);
  const passwordValidation = useValidateInput(PASSWORD);
  const nicknameValidation = useValidateInput(NICKNAME);
  const phoneNumberValidation = useValidateInput(PHONE_NUMBER);
  const birthdayValidation = useValidateInput(BIRTHDAY);
  const genderValidation = useValidateInput(GENDER);

  const [formState, setFormState] = useState<MemberJoinRequest>({
    identifier: '',
    password: '',
    nickname: '',
    phoneNumber: '',
    genderType: '',
    birthday: '',
  });

  useEffect(() => {
    setFormState({
      identifier: identifierValidation.value,
      password: passwordValidation.value,
      nickname: nicknameValidation.value,
      phoneNumber: phoneNumberValidation.value,
      genderType: genderValidation.value,
      birthday: birthdayValidation.value,
    });
  }, [
    identifierValidation.value,
    passwordValidation.value,
    nicknameValidation.value,
    phoneNumberValidation.value,
    birthdayValidation.value,
    genderValidation.value,
  ]);

  const validateForm = () => {
    const keys = getInvariantObjectKeys(invariantOf(formState));

    const getValidationObjectForKey = (key: string) => {
      switch (key) {
        case 'identifier':
          return identifierValidation;
        case 'password':
          return passwordValidation;
        case 'nickname':
          return nicknameValidation;
        case 'phoneNumber':
          return phoneNumberValidation;
        case 'birthday':
          return birthdayValidation;
        case 'genderType':
          return genderValidation;
        default:
          return null;
      }
    };

    const hasInvalidField = keys.some((key) => {
      const validationObj = getValidationObjectForKey(key);

      if (!formState[key] || (validationObj && validationObj.errorMessage)) {
        if (validationObj) {
          validationObj.setErrorMessage(
            validationObj.errorMessage || `${key} 를 입력해주세요`
          );
        }
        return true;
      }

      return false;
    });

    return !hasInvalidField;
  };

  return {
    formState,
    validateForm,
    identifierValidation,
    passwordValidation,
    nicknameValidation,
    phoneNumberValidation,
    birthdayValidation,
    genderValidation,
  };
};

export default useSignUpFormValidation;
