import { ValidationsType } from '@hooks/_common/useFormInput';
import { isCurrentOrFutureDate } from '@utils/_common/isCurrentOrFutureDate';

const isEmpty = (value: string) => value.length === 0;
const isNumber = (value: string) => /^[1-9]+\d*$/.test(value);
const isMaxLength = (value: string, length: number) => value.length <= length;

export const staticValidations: ValidationsType = {
  name: (inputValue) => {
    if (isEmpty(inputValue)) {
      return { ok: false, message: '골룸 이름은 필수 항목입니다', updateOnFail: true };
    }

    return { ok: true };
  },

  limitedMemberCount: (inputValue) => {
    if (isEmpty(inputValue)) {
      return { ok: false, message: '최대 인원수는 필수 항목입니다', updateOnFail: true };
    }

    if (!isNumber(inputValue)) {
      return { ok: false, message: '숫자를 입력해주세요', updateOnFail: false };
    }

    return { ok: true };
  },

  'goalRoomTodo[content]': (inputValue) => {
    if (isEmpty(inputValue)) {
      return { ok: false, message: '투두 리스트는 필수 항목입니다', updateOnFail: true };
    }

    if (!isMaxLength(inputValue, 10)) {
      return {
        ok: false,
        message: '최대 10글자까지 작성할 수 있습니다',
        updateOnFail: false,
      };
    }

    return { ok: true };
  },

  'goalRoomTodo[startDate]': (inputValue) => {
    if (isEmpty(inputValue)) {
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
    if (isEmpty(inputValue)) {
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
      if (isEmpty(inputValue)) {
        return {
          ok: false,
          message: '인증 횟수는 필수 항목입니다',
          updateOnFail: true,
        };
      }

      if (!isNumber(inputValue)) {
        return { ok: false, message: '숫자를 입력해주세요', updateOnFail: false };
      }

      return { ok: true };
    };

    validations[startDateKey] = (inputValue) => {
      if (isEmpty(inputValue)) {
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
      if (isEmpty(inputValue)) {
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
