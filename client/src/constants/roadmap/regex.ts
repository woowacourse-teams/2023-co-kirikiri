export const DESCRIPTION_MAX_LENGTH = {
  rule: /^.{1,150}$/,
  message: '소개글을 입력해주세요',
};

export const MAIN_TEXT_MAX_LENGTH = { rule: /^.{0,2000}$/, message: '' };

export const PERIOD = {
  rule: /^(?:(?:[1-9]\d{0,2}|1000)|)$/,
  message: '1일부터 1000일까지만 입력할 수 있습니다',
};

export const TITLE_MAX_LENGTH = {
  rule: /^.{1,20}$/,
  message: '제목은 필수로 입력해주세요',
};
