import { isEmptyString } from '@utils/_common/isEmptyString';
import { isValidMaxLength } from '@utils/_common/isValidMaxLength';
import { isCurrentOrFutureDate } from '@utils/_common/isCurrentOrFutureDate';
import { isNumeric } from '@utils/_common/isNumeric';
import { isValidMaxValue } from '@utils/_common/isValidMaxValue';
import { ValidationsType } from '@hooks/_common/useFormInput';

export const staticValidations: ValidationsType = {
  name: (inputValue) => {
    if (isEmptyString(inputValue)) {
      return { ok: false, message: '골룸 이름은 필수 항목입니다', updateOnFail: true };
    }

    if (!isValidMaxLength(inputValue, 40)) {
      return {
        ok: false,
        message: `최대 ${40}글자까지 작성할 수 있습니다`,
        updateOnFail: false,
      };
    }

    return { ok: true };
  },

  limitedMemberCount: (inputValue) => {
    if (isEmptyString(inputValue)) {
      return { ok: false, message: '최대 인원수는 필수 항목입니다', updateOnFail: true };
    }

    if (!isNumeric(inputValue)) {
      return { ok: false, message: '숫자를 입력해주세요', updateOnFail: false };
    }

    if (!isValidMaxValue(inputValue, 20)) {
      return { ok: false, message: '최대 인원수는 20입니다', updateOnFail: false };
    }

    return { ok: true };
  },

  'goalRoomTodo[content]': (inputValue) => {
    if (isEmptyString(inputValue)) {
      return { ok: false, message: '투두 리스트는 필수 항목입니다', updateOnFail: true };
    }

    if (!isValidMaxLength(inputValue, 40)) {
      return {
        ok: false,
        message: `최대 ${40}글자까지 작성할 수 있습니다`,
        updateOnFail: false,
      };
    }

    return { ok: true };
  },

  'goalRoomTodo[startDate]': (inputValue) => {
    if (isEmptyString(inputValue)) {
      return { ok: false, message: '시작일은 필수 항목입니다', updateOnFail: true };
    }

    if (!isCurrentOrFutureDate(inputValue)) {
      return {
        ok: false,
        message: '이전 날짜를 입력할 수 없습니다',
        updateOnFail: false,
      };
    }

    return { ok: true };
  },

  'goalRoomTodo[endDate]': (inputValue) => {
    if (isEmptyString(inputValue)) {
      return { ok: false, message: '종료일은 필수 항목입니다', updateOnFail: true };
    }

    if (!isCurrentOrFutureDate(inputValue)) {
      return {
        ok: false,
        message: '이전 날짜를 입력할 수 없습니다',
        updateOnFail: false,
      };
    }

    return { ok: true };
  },
};

export const generateNodesValidations = (nodes: any[]) => {
  const validations: ValidationsType = {};

  nodes.forEach((_, index) => {
    const checkCountKey = `goalRoomRoadmapNodeRequests[${index}][checkCount]`;
    const startDateKey = `goalRoomRoadmapNodeRequests[${index}][startDate]`;
    const endDateKey = `goalRoomRoadmapNodeRequests[${index}][endDate]`;

    validations[checkCountKey] = (inputValue) => {
      if (isEmptyString(inputValue)) {
        return {
          ok: false,
          message: '인증 횟수는 필수 항목입니다',
          updateOnFail: true,
        };
      }

      if (!isNumeric(inputValue)) {
        return { ok: false, message: '숫자를 입력해주세요', updateOnFail: false };
      }

      return { ok: true };
    };

    validations[startDateKey] = (inputValue) => {
      if (isEmptyString(inputValue)) {
        return { ok: false, message: '시작일은 필수 항목입니다', updateOnFail: true };
      }

      if (!isCurrentOrFutureDate(inputValue)) {
        return {
          ok: false,
          message: '이전 날짜를 입력할 수 없습니다',
          updateOnFail: false,
        };
      }

      return { ok: true };
    };

    validations[endDateKey] = (inputValue) => {
      if (isEmptyString(inputValue)) {
        return { ok: false, message: '종료일은 필수 항목입니다', updateOnFail: true };
      }

      if (!isCurrentOrFutureDate(inputValue)) {
        return {
          ok: false,
          message: '이전 날짜를 입력할 수 없습니다',
          updateOnFail: false,
        };
      }

      return { ok: true };
    };
  });

  return validations;
};
