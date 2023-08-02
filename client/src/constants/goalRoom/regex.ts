export const TODO_CONTENT_MAX_LENGTH = {
  rule: /^.{1,250}$/,
  message: '1글자부터 250글자까지 작성해주세요',
};

export const TODO_START_DATE = {
  rule: /^(19|20)\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/,
  message: 'yyyy-mm-dd 형식으로 입력해주세요 (ex. 2021-01-01)',
};

export const TODO_END_DATE = {
  rule: /^(19|20)\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/,
  message: 'yyyy-mm-dd 형식으로 입력해주세요 (ex. 2021-01-01)',
};
