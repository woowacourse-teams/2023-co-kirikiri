import SVGIcon from '@/components/icons/SVGIcon';

export const TOAST_CONTENTS = {
  CREATE_GOALROOM: {
    success: {
      message: '모임을 생성했습니다!',
      indicator: <SVGIcon name='SuccessIcon' />,
    },
    error: {
      message: '모임을 생성하지 못했습니다 😭',
      indicator: <SVGIcon name='ErrorIcon' />,
    },
  },
  CREATE_TODO: {
    success: {
      message: '새로운 투두리스트가 등록되었습니다.',
      indicator: <SVGIcon name='SuccessIcon' />,
    },
    error: {
      message: '투두리스트 등록에 실패했습니다 😭',
      indicator: <SVGIcon name='ErrorIcon' />,
    },
  },
  CHECK_TODO: {
    success: {
      message: '투두리스트 상태 변경 완료!',
      indicator: <SVGIcon name='SuccessIcon' />,
    },
    error: {
      message: '다시한번 시도해주세요 😭',
      indicator: <SVGIcon name='ErrorIcon' />,
    },
  },
  CREATE_FEED: {
    success: {
      message: '인증 피드가 등록되었습니다.',
      indicator: <SVGIcon name='SuccessIcon' />,
    },
    error: {
      message: '인증피드 등록에 실패했습니다 😭',
      indicator: <SVGIcon name='ErrorIcon' />,
    },
  },
  JOIN_GOALROOM: {
    success: {
      message: '모임에 참여하였습니다!',
      indicator: <SVGIcon name='SuccessIcon' />,
    },
    error: {
      message: '모임 참여에 실패했습니다 😭',
      indicator: <SVGIcon name='ErrorIcon' />,
    },
  },
  START_GOALROOM: {
    success: {
      message: '모임이 시작되었습니다.',
      indicator: <SVGIcon name='SuccessIcon' />,
    },
    error: {
      message: '모임 시작에 실패했습니다 😭',
      indicator: <SVGIcon name='ErrorIcon' />,
    },
  },
  CREATE_ROADMAP: {
    success: {
      message: '로드맵이 생성되었습니다!',
      indicator: <SVGIcon name='SuccessIcon' />,
    },
    error: {
      message: '로드맵을 생성하지 못했습니다 😭',
      indicator: <SVGIcon name='ErrorIcon' />,
    },
  },
  SIGN_UP: {
    success: {
      message: '회원가입 성공!',
      indicator: <SVGIcon name='SuccessIcon' />,
    },
    error: {
      message: '오류가 발생했습니다 😭',
      indicator: <SVGIcon name='ErrorIcon' />,
    },
  },
  LOGIN: {
    success: {
      message: '로그인 성공!',
      indicator: <SVGIcon name='SuccessIcon' />,
    },
    error: {
      message: '존재하지 않는 계정입니다.',
      indicator: <SVGIcon name='ErrorIcon' />,
    },
  },
  LOGOUT: {
    success: {
      message: '로그아웃 성공!',
      indicator: <SVGIcon name='SuccessIcon' />,
    },
    error: {
      message: '오류가 발생했습니다 😭',
      indicator: <SVGIcon name='ErrorIcon' />,
    },
  },
  PRIVATE_PAGE: {
    success: {
      message: '로그인이 필요한 서비스입니다.',
      indicator: <SVGIcon name='WarningIcon' />,
    },
    error: {
      message: '오류가 발생했습니다 😭',
      indicator: <SVGIcon name='ErrorIcon' />,
    },
  },
};

export const NETWORK_ERROR = {
  message: '네트워크 연결에 문제가 발생했습니다 🚨',
  indicator: <SVGIcon name='WarningIcon' />,
};
