export const IDENTIFIER = {
  rule: /^[a-z0-9]{4,20}$/,
  message: '-아이디는 영어 소문자와 숫자만 포함할 수 있으며, 4~20자여야 합니다.',
};

export const PASSWORD = {
  rule: /^[a-z0-9!@#$%^&*()~]{8,15}$/,
  message:
    '-비밀번호는 8~15자리여야 하며, 영어 소문자, 숫자, [!,@,#,$,%,^,&,*,(,),~] 특수문자만 포함해야 합니다.',
};

export const NICKNAME = {
  rule: /^.{2,8}$/,
  message: '-닉네임은 2~8자리여야 합니다.',
};

export const GENDER = {
  rule: /^(MALE|FEMALE)$/,
  message: "-성별은 '남자' 또는 '여자'만 선택 가능합니다.",
};
