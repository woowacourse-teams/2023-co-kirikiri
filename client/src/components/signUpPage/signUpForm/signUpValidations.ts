import { ValidationsType } from '@hooks/_common/useFormInput';

import { EMAIL, GENDER, IDENTIFIER, NICKNAME, PASSWORD } from '@constants/user/regex';

export const staticValidations: ValidationsType = {
  identifier: (inputValue) => {
    if (!IDENTIFIER.rule.test(inputValue)) {
      return {
        ok: false,
        message: IDENTIFIER.message,
        updateOnFail: true,
      };
    }

    return { ok: true };
  },

  password: (inputValue) => {
    if (!PASSWORD.rule.test(inputValue)) {
      return {
        ok: false,
        message: PASSWORD.message,
        updateOnFail: true,
      };
    }

    return { ok: true };
  },

  email: (inputValue) => {
    if (!EMAIL.rule.test(inputValue)) {
      return {
        ok: false,
        message: EMAIL.message,
        updateOnFail: true,
      };
    }

    return { ok: true };
  },

  nickname: (inputValue) => {
    if (!NICKNAME.rule.test(inputValue)) {
      return {
        ok: false,
        message: NICKNAME.message,
        updateOnFail: true,
      };
    }

    return { ok: true };
  },

  genderType: (inputValue) => {
    if (!GENDER.rule.test(inputValue)) {
      return {
        ok: false,
        message: GENDER.message,
        updateOnFail: true,
      };
    }

    return { ok: true };
  },
};
