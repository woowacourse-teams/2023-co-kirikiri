import { isEmptyString } from '@utils/_common/isEmptyString';
import { isValidMaxLength } from '@utils/_common/isValidMaxLength';
import { isCurrentOrFutureDate } from '@utils/_common/isCurrentOrFutureDate';
import { isNumeric } from '@utils/_common/isNumeric';
import { isValidMaxValue } from '@utils/_common/isValidMaxValue';
import { ValidationsType } from '@hooks/_common/useFormInput';

import { GOALROOM, ERROR_MESSAGE } from '@constants/goalRoom/goalRoomValidation';

const isValidDate = (date: string) => /^\d{4}-\d{2}-\d{2}$/.test(date);

const isValidStartDate = (prevNodeEndDate: string, startDate: string) => {
  if (!isValidDate(startDate)) return false;

  const prevNodeEnd = new Date(prevNodeEndDate);
  const start = new Date(startDate);

  prevNodeEnd.setHours(0, 0, 0, 0);
  start.setHours(0, 0, 0, 0);

  return prevNodeEnd < start;
};

const isValidEndDate = (startDate: string, endDate: string) => {
  if (!isValidDate(endDate)) return false;

  const start = new Date(startDate);
  const end = new Date(endDate);

  start.setHours(0, 0, 0, 0);
  end.setHours(0, 0, 0, 0);

  return start <= end;
};

export const staticValidations: ValidationsType = {
  name: (inputValue) => {
    if (isEmptyString(inputValue)) {
      return { ok: false, message: ERROR_MESSAGE.NAME_REQUIRED, updateOnFail: true };
    }

    if (!isValidMaxLength(inputValue, GOALROOM.MAX_NAME_LENGTH)) {
      return {
        ok: false,
        message: ERROR_MESSAGE.NAME_MAX_LENGTH,
        updateOnFail: false,
      };
    }

    return { ok: true };
  },

  limitedMemberCount: (inputValue) => {
    if (isEmptyString(inputValue)) {
      return {
        ok: false,
        message: ERROR_MESSAGE.MEMBER_COUNT_REQUIRED,
        updateOnFail: true,
      };
    }

    if (!isNumeric(inputValue)) {
      return { ok: false, message: ERROR_MESSAGE.NUMERIC, updateOnFail: false };
    }

    if (!isValidMaxValue(inputValue, GOALROOM.MEMBER_COUNT_MAX_VALUE)) {
      return {
        ok: false,
        message: ERROR_MESSAGE.MEMBER_COUNT_MAX_VALUE,
        updateOnFail: false,
      };
    }

    return { ok: true };
  },

  'goalRoomTodo[content]': (inputValue) => {
    if (isEmptyString(inputValue)) {
      return {
        ok: false,
        message: ERROR_MESSAGE.TODO_CONTENT_REQUIRED,
        updateOnFail: true,
      };
    }

    if (!isValidMaxLength(inputValue, GOALROOM.TODO_CONTENT_MAX_LENGTH)) {
      return {
        ok: false,
        message: ERROR_MESSAGE.TODO_CONTENT_MAX_LENGTH,
        updateOnFail: false,
      };
    }

    return { ok: true };
  },

  'goalRoomTodo[startDate]': (inputValue) => {
    if (isEmptyString(inputValue)) {
      return {
        ok: false,
        message: ERROR_MESSAGE.START_DATE_REQUIRED,
        updateOnFail: true,
      };
    }

    if (!isCurrentOrFutureDate(inputValue)) {
      return {
        ok: false,
        message: ERROR_MESSAGE.INVALID_DATE,
        updateOnFail: false,
      };
    }

    return { ok: true };
  },

  'goalRoomTodo[endDate]': (inputValue, formState) => {
    if (isEmptyString(inputValue)) {
      return { ok: false, message: ERROR_MESSAGE.END_DATE_REQUIRED, updateOnFail: true };
    }

    if (!isCurrentOrFutureDate(inputValue)) {
      return {
        ok: false,
        message: ERROR_MESSAGE.INVALID_DATE,
        updateOnFail: false,
      };
    }

    const startDate = formState.goalRoomTodo?.startDate;
    if (!startDate) {
      return {
        ok: false,
        message: ERROR_MESSAGE.NEED_START_DATE,
        updateOnFail: false,
      };
    } else if (!isValidEndDate(startDate, inputValue)) {
      return {
        ok: false,
        message: ERROR_MESSAGE.INVALID_END_DATE,
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
          message: ERROR_MESSAGE.CHECK_COUNT_REQUIRED,
          updateOnFail: true,
        };
      }

      if (!isNumeric(inputValue)) {
        return { ok: false, message: ERROR_MESSAGE.NUMERIC, updateOnFail: false };
      }

      return { ok: true };
    };

    validations[startDateKey] = (inputValue, formState: any) => {
      if (isEmptyString(inputValue)) {
        return {
          ok: false,
          message: ERROR_MESSAGE.START_DATE_REQUIRED,
          updateOnFail: true,
        };
      }

      if (!isCurrentOrFutureDate(inputValue)) {
        return {
          ok: false,
          message: ERROR_MESSAGE.INVALID_DATE,
          updateOnFail: false,
        };
      }

      const prevNodeEndDate = formState.goalRoomRoadmapNodeRequests[index - 1]?.endDate;
      if (prevNodeEndDate && !isValidStartDate(prevNodeEndDate, inputValue)) {
        return {
          ok: false,
          message: ERROR_MESSAGE.NEED_HIGHER_THAN_PREV_DATE,
          updateOnFail: false,
        };
      } else if (!prevNodeEndDate && index !== 0) {
        return {
          ok: false,
          message: ERROR_MESSAGE.NEED_PREV_DATE,
          updateOnFail: false,
        };
      }

      return { ok: true };
    };

    validations[endDateKey] = (inputValue, formState: any) => {
      if (isEmptyString(inputValue)) {
        return {
          ok: false,
          message: ERROR_MESSAGE.END_DATE_REQUIRED,
          updateOnFail: true,
        };
      }

      if (!isCurrentOrFutureDate(inputValue)) {
        return {
          ok: false,
          message: ERROR_MESSAGE.INVALID_DATE,
          updateOnFail: false,
        };
      }

      const startDate = formState.goalRoomRoadmapNodeRequests[index]?.startDate;
      if (!startDate) {
        return {
          ok: false,
          message: ERROR_MESSAGE.NEED_START_DATE,
          updateOnFail: false,
        };
      } else if (!isValidEndDate(startDate, inputValue)) {
        return {
          ok: false,
          message: ERROR_MESSAGE.INVALID_END_DATE,
          updateOnFail: false,
        };
      }

      return { ok: true };
    };
  });

  return validations;
};
