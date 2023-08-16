export const GOALROOM = {
  MAX_NAME_LENGTH: 40,

  MEMBER_COUNT_MAX_VALUE: 20,

  TODO_CONTENT_MAX_LENGTH: 250,
};

export const ERROR_MESSAGE = {
  NAME_REQUIRED: '골룸 이름은 필수 항목입니다',
  NAME_MAX_LENGTH: `최대 ${GOALROOM.MAX_NAME_LENGTH}글자까지 입력할 수 있습니다`,

  MEMBER_COUNT_REQUIRED: '최대 인원수는 필수 항목입니다',
  MEMBER_COUNT_MAX_VALUE: `최대 인원수는 ${GOALROOM.MEMBER_COUNT_MAX_VALUE}입니다`,

  TODO_CONTENT_REQUIRED: '투두 리스트는 필수 항목입니다',
  TODO_CONTENT_MAX_LENGTH: `최대 ${GOALROOM.TODO_CONTENT_MAX_LENGTH}글자까지 입력할 수 있습니다`,

  START_DATE_REQUIRED: '시작일은 필수 항목입니다',
  END_DATE_REQUIRED: '종료일은 필수 항목입니다',
  INVALID_DATE: '이전 날짜를 입력할 수 없습니다',

  CHECK_COUNT_REQUIRED: '인증 횟수는 필수 항목입니다',

  NUMERIC: '숫자를 입력해주세요',
};
