import { ValidationsType } from '@hooks/_common/useFormInput';
import { NodeType } from '@myTypes/roadmap/internal';

export const staticValidations = {
  name: (inputValue: string) => {
    if (inputValue.length === 0) {
      return { ok: false, message: '이름은 필수 항목입니다', updateOnFail: true };
    }

    return { ok: true };
  },

  limitedMemberCount: (inputValue: string) => {
    if (inputValue.length === 0) {
      return { ok: false, message: '최대 인원수는 필수 항목입니다', updateOnFail: true };
    }

    if (!/^[1-9]+\d$/.test(inputValue)) {
      return { ok: false, message: '숫자를 입력해주세요', updateOnFail: false };
    }

    return { ok: true };
  },

  'goalRoomTodo[content]': (inputValue: string) => {
    if (inputValue.length === 0) {
      return { ok: false, message: '투두 리스트는 필수 항목입니다', updateOnFail: true };
    }

    if (inputValue.length > 250) {
      return {
        ok: false,
        message: '최대 250글자까지 작성할 수 있습니다',
        updateOnFail: false,
      };
    }

    return { ok: true };
  },

  'goalRoomTodo[startDate]': (inputValue: string) => {
    if (inputValue.length === 0) {
      return { ok: false, message: '투두 리스트는 필수 항목입니다', updateOnFail: true };
    }

    return { ok: true };
  },

  'goalRoomTodo[endDate]': (inputValue: string) => {
    if (inputValue.length === 0) {
      return { ok: false, message: '투두 리스트는 필수 항목입니다', updateOnFail: true };
    }

    if (inputValue.length > 250) {
      return {
        ok: false,
        message: '최대 250글자까지 작성할 수 있습니다',
        updateOnFail: false,
      };
    }

    return { ok: true };
  },
};

export const generateNodesValidations = (nodes: NodeType[]) => {
  const validations: ValidationsType = {};

  nodes.forEach((_, index) => {
    validations[`goalRoomRoadmapNodeRequests[${index}][checkCount]`] = (
      inputValue: string
    ) => {
      if (inputValue.length === 0) {
        return { ok: false, message: '체크 횟수는 필수 항목입니다', updateOnFail: true };
      }

      if (!/^[1-9]\d*$/.test(inputValue)) {
        return { ok: false, message: '숫자 형식을 입력해주세요', updateOnFail: false };
      }

      return { ok: true };
    };

    validations[`goalRoomRoadmapNodeRequests[${index}][startDate]`] = (
      inputValue: string
    ) => {
      if (inputValue.length === 0) {
        return { ok: false, message: '시작일은 필수 항목입니다', updateOnFail: true };
      }

      return { ok: true };
    };

    validations[`goalRoomRoadmapNodeRequests[${index}][endDate]`] = (
      inputValue: string
    ) => {
      if (inputValue.length === 0) {
        return { ok: false, message: '종료일은 필수 항목입니다', updateOnFail: true };
      }

      return { ok: true };
    };
  });

  return validations;
};
