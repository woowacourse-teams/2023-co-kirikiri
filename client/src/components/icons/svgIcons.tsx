import { SVGProps } from 'react';

export const PersonIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 24 24'
  >
    <path
      fill='currentColor'
      d='M12 12q-1.65 0-2.825-1.175T8 8q0-1.65 1.175-2.825T12 4q1.65 0 2.825 1.175T16 8q0 1.65-1.175 2.825T12 12Zm-8 8v-2.8q0-.85.438-1.563T5.6 14.55q1.55-.775 3.15-1.163T12 13q1.65 0 3.25.388t3.15 1.162q.725.375 1.163 1.088T20 17.2V20H4Zm2-2h12v-.8q0-.275-.138-.5t-.362-.35q-1.35-.675-2.725-1.012T12 15q-1.4 0-2.775.338T6.5 16.35q-.225.125-.363.35T6 17.2v.8Zm6-8q.825 0 1.413-.588T14 8q0-.825-.588-1.413T12 6q-.825 0-1.413.588T10 8q0 .825.588 1.413T12 10Zm0-2Zm0 10Z'
    />
  </svg>
);

export const LockIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 24 24'
  >
    <path
      fill='currentColor'
      d='M12 17a2 2 0 0 1-2-2c0-1.11.89-2 2-2a2 2 0 0 1 2 2a2 2 0 0 1-2 2m6 3V10H6v10h12m0-12a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V10c0-1.11.89-2 2-2h1V6a5 5 0 0 1 5-5a5 5 0 0 1 5 5v2h1m-6-5a3 3 0 0 0-3 3v2h6V6a3 3 0 0 0-3-3Z'
    />
  </svg>
);

export const StandingPersonIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 32 32'
  >
    <path
      fill='currentColor'
      d='M18 30h-4a2 2 0 0 1-2-2v-7a2 2 0 0 1-2-2v-6a3 3 0 0 1 3-3h6a3 3 0 0 1 3 3v6a2 2 0 0 1-2 2v7a2 2 0 0 1-2 2zm-5-18a.94.94 0 0 0-1 1v6h2v9h4v-9h2v-6a.94.94 0 0 0-1-1zm3-3a4 4 0 1 1 4-4a4 4 0 0 1-4 4zm0-6a2 2 0 1 0 2 2a2 2 0 0 0-2-2z'
    />
  </svg>
);

export const PhoneIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 256 256'
  >
    <path
      fill='currentColor'
      d='m222.37 158.46l-47.11-21.11l-.13-.06a16 16 0 0 0-15.17 1.4a8.12 8.12 0 0 0-.75.56L134.87 160c-15.42-7.49-31.34-23.29-38.83-38.51l20.78-24.71c.2-.25.39-.5.57-.77a16 16 0 0 0 1.32-15.06v-.12L97.54 33.64a16 16 0 0 0-16.62-9.52A56.26 56.26 0 0 0 32 80c0 79.4 64.6 144 144 144a56.26 56.26 0 0 0 55.88-48.92a16 16 0 0 0-9.51-16.62ZM176 208A128.14 128.14 0 0 1 48 80a40.2 40.2 0 0 1 34.87-40a.61.61 0 0 0 0 .12l21 47l-20.67 24.74a6.13 6.13 0 0 0-.57.77a16 16 0 0 0-1 15.7c9.06 18.53 27.73 37.06 46.46 46.11a16 16 0 0 0 15.75-1.14a8.44 8.44 0 0 0 .74-.56L168.89 152l47 21.05h.11A40.21 40.21 0 0 1 176 208Z'
    />
  </svg>
);

export const GenderIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 24 24'
  >
    <path
      fill='none'
      stroke='currentColor'
      d='M16 24v-5.5h-3.5v-.25l.072-.15A24.999 24.999 0 0 0 15 7.35v-.328a8.58 8.58 0 0 1 3-.523c1.288 0 2.311.266 3 .523v.328c0 3.72.83 7.391 2.428 10.749l.072.15v.25H20V24M8 24v-6.5a2 2 0 0 1 2-2v-8s-1.5-1-4-1s-4 1-4 1v8a2 2 0 0 1 2 2V24M17.85 4.5s-1.6-1-1.6-2.25a1.747 1.747 0 1 1 3.496 0c0 1.25-1.596 2.25-1.596 2.25h-.3Zm-12 0s-1.6-1-1.6-2.25a1.747 1.747 0 1 1 3.496 0C7.746 3.5 6.15 4.5 6.15 4.5h-.3Z'
    />
  </svg>
);

export const CalendarIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 24 24'
  >
    <path
      fill='currentColor'
      d='M19 4h-2V3a1 1 0 0 0-2 0v1H9V3a1 1 0 0 0-2 0v1H5a3 3 0 0 0-3 3v12a3 3 0 0 0 3 3h14a3 3 0 0 0 3-3V7a3 3 0 0 0-3-3Zm1 15a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1v-7h16Zm0-9H4V7a1 1 0 0 1 1-1h2v1a1 1 0 0 0 2 0V6h6v1a1 1 0 0 0 2 0V6h2a1 1 0 0 1 1 1Z'
    />
  </svg>
);

export const GoogleIcon = ({ width }: SVGProps<SVGSVGElement>) => (
  <svg
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 48 48'
  >
    <path
      fill='#FFC107'
      d='M43.611 20.083H42V20H24v8h11.303c-1.649 4.657-6.08 8-11.303 8c-6.627 0-12-5.373-12-12s5.373-12 12-12c3.059 0 5.842 1.154 7.961 3.039l5.657-5.657C34.046 6.053 29.268 4 24 4C12.955 4 4 12.955 4 24s8.955 20 20 20s20-8.955 20-20c0-1.341-.138-2.65-.389-3.917z'
    />
    <path
      fill='#FF3D00'
      d='m6.306 14.691l6.571 4.819C14.655 15.108 18.961 12 24 12c3.059 0 5.842 1.154 7.961 3.039l5.657-5.657C34.046 6.053 29.268 4 24 4C16.318 4 9.656 8.337 6.306 14.691z'
    />
    <path
      fill='#4CAF50'
      d='M24 44c5.166 0 9.86-1.977 13.409-5.192l-6.19-5.238A11.91 11.91 0 0 1 24 36c-5.202 0-9.619-3.317-11.283-7.946l-6.522 5.025C9.505 39.556 16.227 44 24 44z'
    />
    <path
      fill='#1976D2'
      d='M43.611 20.083H42V20H24v8h11.303a12.04 12.04 0 0 1-4.087 5.571l.003-.002l6.19 5.238C36.971 39.205 44 34 44 24c0-1.341-.138-2.65-.389-3.917z'
    />
  </svg>
);

export const KakaoIcon = ({ width }: SVGProps<SVGSVGElement>) => (
  <svg
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 256 256'
  >
    <path
      fill='#FFE812'
      d='M256 236c0 11.046-8.954 20-20 20H20c-11.046 0-20-8.954-20-20V20C0 8.954 8.954 0 20 0h216c11.046 0 20 8.954 20 20v216z'
    />
    <path d='M128 36C70.562 36 24 72.713 24 118c0 29.279 19.466 54.97 48.748 69.477-1.593 5.494-10.237 35.344-10.581 37.689 0 0-.207 1.762.934 2.434s2.483.15 2.483.15c3.272-.457 37.943-24.811 43.944-29.04 5.995.849 12.168 1.29 18.472 1.29 57.438 0 104-36.712 104-82 0-45.287-46.562-82-104-82z' />
    <path
      fill='#FFE812'
      d='M70.5 146.625c-3.309 0-6-2.57-6-5.73V105.25h-9.362c-3.247 0-5.888-2.636-5.888-5.875s2.642-5.875 5.888-5.875h30.724c3.247 0 5.888 2.636 5.888 5.875s-2.642 5.875-5.888 5.875H76.5v35.645c0 3.16-2.691 5.73-6 5.73zm52.612-.078c-2.502 0-4.416-1.016-4.993-2.65l-2.971-7.778-18.296-.001-2.973 7.783c-.575 1.631-2.488 2.646-4.99 2.646a9.155 9.155 0 0 1-3.814-.828c-1.654-.763-3.244-2.861-1.422-8.52l14.352-37.776c1.011-2.873 4.082-5.833 7.99-5.922 3.919.088 6.99 3.049 8.003 5.928l14.346 37.759c1.826 5.672.236 7.771-1.418 8.532a9.176 9.176 0 0 1-3.814.827c-.001 0 0 0 0 0zm-11.119-21.056L106 108.466l-5.993 17.025h11.986zM138 145.75c-3.171 0-5.75-2.468-5.75-5.5V99.5c0-3.309 2.748-6 6.125-6s6.125 2.691 6.125 6v35.25h12.75c3.171 0 5.75 2.468 5.75 5.5s-2.579 5.5-5.75 5.5H138zm33.334.797c-3.309 0-6-2.691-6-6V99.5c0-3.309 2.691-6 6-6s6 2.691 6 6v12.896l16.74-16.74c.861-.861 2.044-1.335 3.328-1.335 1.498 0 3.002.646 4.129 1.772 1.051 1.05 1.678 2.401 1.764 3.804.087 1.415-.384 2.712-1.324 3.653l-13.673 13.671 14.769 19.566a5.951 5.951 0 0 1 1.152 4.445 5.956 5.956 0 0 1-2.328 3.957 5.94 5.94 0 0 1-3.609 1.211 5.953 5.953 0 0 1-4.793-2.385l-14.071-18.644-2.082 2.082v13.091a6.01 6.01 0 0 1-6.002 6.003z'
    />
  </svg>
);

export const BackArrowIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 1024 1024'
  >
    <path fill='currentColor' d='M224 480h640a32 32 0 1 1 0 64H224a32 32 0 0 1 0-64z' />
    <path
      fill='currentColor'
      d='m237.248 512l265.408 265.344a32 32 0 0 1-45.312 45.312l-288-288a32 32 0 0 1 0-45.312l288-288a32 32 0 1 1 45.312 45.312L237.248 512z'
    />
  </svg>
);

export const LanguageIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 24 24'
  >
    <g fill='none' stroke='currentColor' strokeLinecap='round' strokeWidth='2'>
      <path
        strokeLinejoin='round'
        d='M14 19c3.771 0 5.657 0 6.828-1.172C22 16.657 22 14.771 22 11c0-3.771 0-5.657-1.172-6.828C19.657 3 17.771 3 14 3h-4C6.229 3 4.343 3 3.172 4.172C2 5.343 2 7.229 2 11c0 3.771 0 5.657 1.172 6.828c.653.654 1.528.943 2.828 1.07'
      />
      <path d='M14 19c-1.236 0-2.598.5-3.841 1.145c-1.998 1.037-2.997 1.556-3.489 1.225c-.492-.33-.399-1.355-.212-3.404L6.5 17.5' />
      <path
        strokeLinejoin='round'
        d='m5.5 13.5l1-2m0 0l1.106-2.211a1 1 0 0 1 1.788 0L10.5 11.5m-4 0h4m0 0l1 2m1-6h1.982V9c0 .5-.496 1.5-1.487 1.5m3.964-3v2m0 0v4m0-4H18.5'
      />
    </g>
  </svg>
);

export const ITIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 48 48'
  >
    <g
      fill='none'
      stroke='currentColor'
      strokeLinecap='round'
      strokeLinejoin='round'
      strokeWidth='4'
    >
      <path d='M21 6H9a3 3 0 0 0-3 3v22a3 3 0 0 0 3 3h30a3 3 0 0 0 3-3V21M24 34v8' />
      <path d='m32 6l-4 4l4 4m6-8l4 4l-4 4M14 42h20' />
    </g>
  </svg>
);

export const ExamIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 256 256'
  >
    <path
      fill='currentColor'
      d='M216 36H40a20 20 0 0 0-20 20v160a12 12 0 0 0 17.37 10.73L64 213.42l26.63 13.31a12 12 0 0 0 10.74 0L128 213.42l26.63 13.31a12 12 0 0 0 10.74 0L192 213.42l26.63 13.31A12 12 0 0 0 236 216V56a20 20 0 0 0-20-20Zm-4 160.58l-14.63-7.31a12 12 0 0 0-10.74 0L160 202.58l-26.63-13.31a12 12 0 0 0-10.74 0L96 202.58l-26.63-13.31a12 12 0 0 0-10.74 0L44 196.58V60h168ZM63.19 171A12 12 0 0 0 79 164.81l2.1-4.81h29.8l2.11 4.81a12 12 0 0 0 22-9.62l-28-64a12 12 0 0 0-22 0l-28 64A12 12 0 0 0 63.19 171Zm37.21-35h-8.8l4.4-10.06Zm35.6-8a12 12 0 0 1 12-12h8v-8a12 12 0 0 1 24 0v8h8a12 12 0 0 1 0 24h-8v8a12 12 0 0 1-24 0v-8h-8a12 12 0 0 1-12-12Z'
    />
  </svg>
);

export const ExerciseIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 48 48'
  >
    <path
      fill='currentColor'
      fillRule='evenodd'
      d='M10.085 6.972a1 1 0 0 0-1.081-.911L7.01 6.23a1 1 0 0 0-.912 1.081l.329 3.869a1 1 0 0 0 .146 1.725l.35 4.122a1 1 0 0 0 1.081.912l1.993-.17a1 1 0 0 0 .912-1.08l-.37-4.346c.51-.091 1.01-.178 1.504-.26c.105 1.65.41 4.449.972 7.085c.36 1.68.852 3.434 1.536 4.814c.34.688.782 1.392 1.367 1.948c.517.492 1.217.927 2.081 1.041v4.332l-.326-.117l-.223-.08c-1.005-.36-2.026-.726-2.806-.923c-.378-.096-.951-.221-1.513-.171a2.406 2.406 0 0 0-1.314.516A2.221 2.221 0 0 0 11 32.263c0 .823.281 2.94.52 4.631a275.893 275.893 0 0 0 .46 3.129l.032.209l.009.055l.002.014v.005L14 40l-1.976.306a2 2 0 0 0 3.97-.163l.37-5.177c1.111.398 2.408.857 3.689 1.217c1.273.358 2.67.66 3.947.66c1.277 0 2.674-.302 3.947-.66c1.27-.357 2.556-.812 3.66-1.207l.399 5.177a2 2 0 0 0 3.97.154L34 40l1.976.306l.001-.005l.002-.014l.009-.055l.032-.21l.114-.753c.094-.628.22-1.48.346-2.375c.239-1.691.52-3.808.52-4.63c0-.59-.23-1.258-.818-1.736a2.405 2.405 0 0 0-1.314-.516c-.562-.05-1.135.075-1.514.17c-.78.198-1.8.564-2.805.924l-.223.08l-.326.117v-4.327c1.89-.211 3.035-1.743 3.666-2.962c.732-1.412 1.21-3.197 1.534-4.886c.508-2.639.708-5.41.773-7.032c.488.08.983.165 1.486.254l-.37 4.339a1 1 0 0 0 .913 1.08l1.992.17a1 1 0 0 0 1.081-.912l.35-4.122a.998.998 0 0 0 .147-1.725l.329-3.869a1 1 0 0 0-.912-1.081l-1.993-.17a1 1 0 0 0-1.08.912l-.287 3.377c-.638-.112-1.263-.217-1.877-.315a2 2 0 0 0-3.161-.453c-6.077-.767-11.222-.766-17.193-.01a2 2 0 0 0-3.144.453c-.616.098-1.243.204-1.883.318l-.286-3.37Zm6.843 11.36a48.495 48.495 0 0 1-.912-6.824c5.531-.673 10.345-.673 15.975.013c-.038 1.332-.208 4.194-.719 6.851c-.3 1.561-.69 2.901-1.157 3.802c-.318.613-.536.785-.608.826H18.643c-.116-.121-.295-.366-.507-.794c-.462-.933-.872-2.304-1.208-3.874ZM28.5 16.5a4.5 4.5 0 1 1-9 0a4.5 4.5 0 0 1 9 0Z'
      clipRule='evenodd'
    />
  </svg>
);

export const GameIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 256 256'
  >
    <path
      fill='currentColor'
      d='M176 116h-24a12 12 0 0 1 0-24h24a12 12 0 0 1 0 24Zm-72-24h-4v-4a12 12 0 0 0-24 0v4h-4a12 12 0 0 0 0 24h4v4a12 12 0 0 0 24 0v-4h4a12 12 0 0 0 0-24Zm140.76 110.94a40 40 0 0 1-61 5.35a7 7 0 0 1-.53-.56L144.67 164h-33.34l-38.52 43.73c-.17.19-.35.38-.53.56a40 40 0 0 1-67.66-35.24a1.18 1.18 0 0 1 0-.2L21 88.79A63.88 63.88 0 0 1 83.88 36H172a64.08 64.08 0 0 1 62.93 52.48a1.8 1.8 0 0 1 0 .19l16.36 84.17a1.77 1.77 0 0 1 0 .2a39.74 39.74 0 0 1-6.53 29.9ZM172 140a40 40 0 0 0 0-80H83.89a39.9 39.9 0 0 0-39.27 33.06a1.55 1.55 0 0 0 0 .21l-16.34 84a16 16 0 0 0 13 18.44a16.07 16.07 0 0 0 13.86-4.21l41.76-47.43a12 12 0 0 1 9-4.07Zm55.76 37.31l-7-35.95a63.84 63.84 0 0 1-44.27 22.46l24.41 27.72a16 16 0 0 0 26.85-14.23Z'
    />
  </svg>
);

export const MusicIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 24 24'
  >
    <g fill='none' fillRule='evenodd'>
      <path d='M24 0v24H0V0h24ZM12.593 23.258l-.011.002l-.071.035l-.02.004l-.014-.004l-.071-.035c-.01-.004-.019-.001-.024.005l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427c-.002-.01-.009-.017-.017-.018Zm.265-.113l-.013.002l-.185.093l-.01.01l-.003.011l.018.43l.005.012l.008.007l.201.093c.012.004.023 0 .029-.008l.004-.014l-.034-.614c-.003-.012-.01-.02-.02-.022Zm-.715.002a.023.023 0 0 0-.027.006l-.006.014l-.034.614c0 .012.007.02.017.024l.015-.002l.201-.093l.01-.008l.004-.011l.017-.43l-.003-.012l-.01-.01l-.184-.092Z' />
      <path
        fill='currentColor'
        d='M18.671 3.208A2 2 0 0 1 21 5.18V17a4 4 0 1 1-2-3.465V9.18L9 10.847V18c0 .06-.005.117-.015.174A3.5 3.5 0 1 1 7 15.337v-8.49a2 2 0 0 1 1.671-1.973l10-1.666ZM9 8.82l10-1.667V5.18L9 6.847V8.82Z'
      />
    </g>
  </svg>
);

export const LifeIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 24 24'
  >
    <path
      fill='currentColor'
      d='M12 12Zm0 9q-.45 0-.863-.163t-.737-.487l-6.7-6.725q-.875-.875-1.288-2T2 9.275Q2 6.7 3.675 4.85T7.85 3q1.2 0 2.263.475T12 4.8q.8-.85 1.863-1.325T16.125 3q2.5 0 4.188 1.85T22 9.25q0 1.225-.425 2.35t-1.275 2l-6.725 6.75q-.325.325-.725.488T12 21Zm1-13q.25 0 .475.125t.35.325l1.7 2.55h4.15q.175-.425.262-.863t.088-.887q-.05-1.725-1.15-2.963t-2.75-1.237q-.775 0-1.488.3t-1.237.875l-.675.725q-.125.15-.325.238t-.4.087q-.2 0-.4-.087t-.35-.238l-.675-.725q-.525-.575-1.225-.9T7.85 5Q6.2 5 5.1 6.263T4 9.25q0 .45.075.888t.25.862H9q.25 0 .475.125t.35.325l.875 1.3l1.35-4.05q.1-.3.362-.5T13 8Zm.3 3.25l-1.35 4.05q-.1.3-.375.5t-.6.2q-.25 0-.475-.125t-.35-.325L8.45 13H5.9l5.925 5.925q.05.05.088.063T12 19q.05 0 .088-.013t.087-.062l5.9-5.925H15q-.25 0-.475-.125t-.375-.325l-.85-1.3Z'
    />
  </svg>
);

export const HobbyIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 24 24'
  >
    <g fill='none' stroke='currentColor' strokeLinejoin='round' strokeWidth='2.5'>
      <circle cx='12' cy='12' r='9' strokeLinecap='round' />
      <path d='m14 12l-3 1.732v-3.464L14 12Z' />
    </g>
  </svg>
);

export const EtcIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns='http://www.w3.org/2000/svg'
    width={width}
    height={width}
    viewBox='0 0 20 20'
  >
    <g fill='#9e9eae'>
      <circle cx='5' cy='10' r='2' />
      <circle cx='10' cy='10' r='2' />
      <circle cx='15' cy='10' r='2' />
    </g>
  </svg>
);

export const VeryDifficultIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    width={width}
    height={width}
    viewBox='0 0 1182 1182'
    fill='none'
    xmlns='http://www.w3.org/2000/svg'
  >
    <mask id='path-1-inside-1_1_669' fill='white'>
      <path d='M0 591C0 513.389 15.2867 436.538 44.9872 364.834C74.6877 293.131 118.22 227.979 173.1 173.1C227.979 118.22 293.131 74.6877 364.834 44.9872C436.538 15.2867 513.389 -3.39249e-06 591 0C668.611 3.3925e-06 745.463 15.2867 817.166 44.9872C888.869 74.6877 954.021 118.22 1008.9 173.1C1063.78 227.979 1107.31 293.131 1137.01 364.834C1166.71 436.538 1182 513.389 1182 591L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 513.389 15.2867 436.538 44.9872 364.834C74.6877 293.131 118.22 227.979 173.1 173.1C227.979 118.22 293.131 74.6877 364.834 44.9872C436.538 15.2867 513.389 -3.39249e-06 591 0C668.611 3.3925e-06 745.463 15.2867 817.166 44.9872C888.869 74.6877 954.021 118.22 1008.9 173.1C1063.78 227.979 1107.31 293.131 1137.01 364.834C1166.71 436.538 1182 513.389 1182 591L591 591H0Z'
      fill='#FF5656'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-1-inside-1_1_669)'
    />
    <mask id='path-2-inside-2_1_669' fill='white'>
      <path d='M0 591C0 466.08 39.583 344.372 113.066 243.351C186.549 142.329 290.152 67.1902 409.002 28.7211C527.851 -9.7481 655.834 -9.56862 774.575 29.2337C893.316 68.0361 996.708 143.466 1069.91 244.693L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 466.08 39.583 344.372 113.066 243.351C186.549 142.329 290.152 67.1902 409.002 28.7211C527.851 -9.7481 655.834 -9.56862 774.575 29.2337C893.316 68.0361 996.708 143.466 1069.91 244.693L591 591H0Z'
      fill='#FF8888'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-2-inside-2_1_669)'
    />
    <mask id='path-3-inside-3_1_669' fill='white'>
      <path d='M0 591C0 497.833 22.0263 405.987 64.2818 322.953C106.537 239.92 167.825 168.054 243.144 113.217C318.462 58.3798 405.679 22.1265 497.677 7.41475C589.674 -7.29697 683.847 -0.0503071 772.511 28.5635L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 497.833 22.0263 405.987 64.2818 322.953C106.537 239.92 167.825 168.054 243.144 113.217C318.462 58.3798 405.679 22.1265 497.677 7.41475C589.674 -7.29697 683.847 -0.0503071 772.511 28.5635L591 591H0Z'
      fill='#FEE114'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-3-inside-3_1_669)'
    />
    <mask id='path-4-inside-4_1_669' fill='white'>
      <path d='M0 591C0 466.369 39.4 344.928 112.568 244.036C185.737 143.144 288.927 67.9646 407.391 29.2449L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 466.369 39.4 344.928 112.568 244.036C185.737 143.144 288.927 67.9646 407.391 29.2449L591 591H0Z'
      fill='#D1D80F'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-4-inside-4_1_669)'
    />
    <mask id='path-5-inside-5_1_669' fill='white'>
      <path d='M0 591C0 465.799 39.7606 343.834 113.549 242.688L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 465.799 39.7606 343.834 113.549 242.688L591 591H0Z'
      fill='#84BD32'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-5-inside-5_1_669)'
    />
    <mask id='path-6-inside-6_1_669' fill='white'>
      <path d='M60 597C60 456.17 115.944 321.108 215.526 221.526C315.108 121.944 450.17 66 591 66C731.83 66 866.892 121.944 966.474 221.526C1066.06 321.108 1122 456.17 1122 597L591 597H60Z' />
    </mask>
    <path
      d='M60 597C60 456.17 115.944 321.108 215.526 221.526C315.108 121.944 450.17 66 591 66C731.83 66 866.892 121.944 966.474 221.526C1066.06 321.108 1122 456.17 1122 597L591 597H60Z'
      fill='url(#paint0_linear_1_669)'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-6-inside-6_1_669)'
    />
    <rect width='35' height='3' transform='matrix(-1 0 0 1 1112 574.925)' fill='white' />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.959262 0.282518 0.282518 0.959262 1097.15 468.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.992069 0.125691 0.125691 0.992069 1109.62 538.867)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.9487 0.316179 0.316179 0.9487 1087.05 434.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.978253 0.207415 0.207415 0.978253 1104.38 501.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.919258 0.393657 0.393657 0.919258 1073.57 400.055)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.883358 0.468699 0.468699 0.883358 1056.08 365.796)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.879184 0.476482 0.476482 0.879184 1040.51 338.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.500135 0.865948 0.865948 0.500135 827.402 137.716)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.235114 0.971968 0.971968 0.235114 732.187 99.5909)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.387326 0.921943 0.921943 0.387326 798.989 123.773)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.200683 0.979656 0.979656 0.200683 697.692 91.3333)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.309648 0.950851 0.950851 0.309648 764.376 109.839)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.118866 0.99291 0.99291 0.118866 660.754 85.5684)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.0359288 0.999354 0.999354 0.0359288 622.339 79.9251)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.0271019 0.999633 0.999633 0.0271019 591.284 79.9251)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.503549 0.863966 0.863966 -0.503549 305.381 164.68)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.727122 0.686508 0.686508 -0.727122 221.276 230.884)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.608149 0.793823 0.793823 -0.608149 275.425 184.89)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.750885 0.660433 0.660433 -0.750885 196.988 256.731)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.671798 0.740734 0.740734 -0.671798 246.149 208.024)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.802998 0.595982 0.595982 -0.802998 173.65 285.938)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.849755 0.527178 0.527178 -0.849755 152.859 318.301)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.854377 0.519653 0.519653 -0.854377 137.483 345.283)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.859029 0.511926 0.511926 0.859029 1026.46 316.656)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.679406 0.733763 0.733763 0.679406 959.446 233.2)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.787872 0.615839 0.615839 0.787872 1005.96 286.898)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.653101 0.757271 0.757271 0.653101 933.363 209.164)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.734167 0.678969 0.678969 0.734167 982.546 257.849)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.588146 0.808755 0.808755 0.588146 903.931 186.111)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.518891 0.85484 0.85484 0.518891 874.436 162.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.511321 0.85939 0.85939 0.511321 851.422 148.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.0136712 0.999907 0.999907 -0.0136712 564.976 81.857)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.295606 0.95531 0.95531 -0.295606 457.788 97.8119)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.139242 0.990258 0.990258 -0.139242 528.953 84.7268)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.329119 0.944288 0.944288 -0.329119 425.648 106.912)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.220769 0.975326 0.975326 -0.220769 492.087 90.4766)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.406187 0.91379 0.91379 -0.406187 391.042 120.144)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.480732 0.876868 0.876868 -0.480732 358.544 135.367)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.488457 0.872588 0.872588 -0.488457 330.286 150.39)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.874851 0.484392 0.484392 -0.874851 125.317 368.63)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.976061 0.217498 0.217498 -0.976061 86.9609 468.556)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.928797 0.37059 0.37059 -0.928797 109.931 401.326)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.983125 0.182934 0.182934 -0.983125 79.3281 503.194)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.956296 0.292401 0.292401 -0.956296 96.625 436.186)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.994898 0.10089 0.10089 -0.994898 74.2324 540.23)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.999841 0.0178498 0.0178498 -0.999841 69.0527 577.388)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.999959 0.00901921 0.00901921 -0.999959 68.832 608.784)'
      fill='white'
    />
    <path
      d='M1017.5 438.119L605.276 559.301L624.765 612.396L1017.5 438.119Z'
      fill='#323232'
    />
    <circle
      cx='44.5'
      cy='44.5'
      r='49.5'
      transform='matrix(-1 0 0 1 636 547)'
      fill='#323232'
      stroke='white'
      strokeWidth='10'
    />
    <defs>
      <linearGradient
        id='paint0_linear_1_669'
        x1='591'
        y1='66'
        x2='591'
        y2='1128'
        gradientUnits='userSpaceOnUse'
      >
        <stop stopColor='#FF5656' />
        <stop offset='0.489583' stopColor='white' />
      </linearGradient>
    </defs>
  </svg>
);

export const difficultIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    width={width}
    height={width}
    viewBox='0 0 1182 1182'
    fill='none'
    xmlns='http://www.w3.org/2000/svg'
  >
    <mask id='path-1-inside-1_1_669' fill='white'>
      <path d='M0 591C0 513.389 15.2867 436.538 44.9872 364.834C74.6877 293.131 118.22 227.979 173.1 173.1C227.979 118.22 293.131 74.6877 364.834 44.9872C436.538 15.2867 513.389 -3.39249e-06 591 0C668.611 3.3925e-06 745.463 15.2867 817.166 44.9872C888.869 74.6877 954.021 118.22 1008.9 173.1C1063.78 227.979 1107.31 293.131 1137.01 364.834C1166.71 436.538 1182 513.389 1182 591L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 513.389 15.2867 436.538 44.9872 364.834C74.6877 293.131 118.22 227.979 173.1 173.1C227.979 118.22 293.131 74.6877 364.834 44.9872C436.538 15.2867 513.389 -3.39249e-06 591 0C668.611 3.3925e-06 745.463 15.2867 817.166 44.9872C888.869 74.6877 954.021 118.22 1008.9 173.1C1063.78 227.979 1107.31 293.131 1137.01 364.834C1166.71 436.538 1182 513.389 1182 591L591 591H0Z'
      fill='#FF5656'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-1-inside-1_1_669)'
    />
    <mask id='path-2-inside-2_1_669' fill='white'>
      <path d='M0 591C0 466.08 39.583 344.372 113.066 243.351C186.549 142.329 290.152 67.1902 409.002 28.7211C527.851 -9.7481 655.834 -9.56862 774.575 29.2337C893.316 68.0361 996.708 143.466 1069.91 244.693L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 466.08 39.583 344.372 113.066 243.351C186.549 142.329 290.152 67.1902 409.002 28.7211C527.851 -9.7481 655.834 -9.56862 774.575 29.2337C893.316 68.0361 996.708 143.466 1069.91 244.693L591 591H0Z'
      fill='#FF8888'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-2-inside-2_1_669)'
    />
    <mask id='path-3-inside-3_1_669' fill='white'>
      <path d='M0 591C0 497.833 22.0263 405.987 64.2818 322.953C106.537 239.92 167.825 168.054 243.144 113.217C318.462 58.3798 405.679 22.1265 497.677 7.41475C589.674 -7.29697 683.847 -0.0503071 772.511 28.5635L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 497.833 22.0263 405.987 64.2818 322.953C106.537 239.92 167.825 168.054 243.144 113.217C318.462 58.3798 405.679 22.1265 497.677 7.41475C589.674 -7.29697 683.847 -0.0503071 772.511 28.5635L591 591H0Z'
      fill='#FEE114'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-3-inside-3_1_669)'
    />
    <mask id='path-4-inside-4_1_669' fill='white'>
      <path d='M0 591C0 466.369 39.4 344.928 112.568 244.036C185.737 143.144 288.927 67.9646 407.391 29.2449L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 466.369 39.4 344.928 112.568 244.036C185.737 143.144 288.927 67.9646 407.391 29.2449L591 591H0Z'
      fill='#D1D80F'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-4-inside-4_1_669)'
    />
    <mask id='path-5-inside-5_1_669' fill='white'>
      <path d='M0 591C0 465.799 39.7606 343.834 113.549 242.688L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 465.799 39.7606 343.834 113.549 242.688L591 591H0Z'
      fill='#84BD32'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-5-inside-5_1_669)'
    />
    <mask id='path-6-inside-6_1_669' fill='white'>
      <path d='M1121 597C1121 456.17 1065.06 321.108 965.474 221.526C865.892 121.944 730.83 66 590 66C449.17 66 314.108 121.944 214.526 221.526C114.945 321.108 59 456.17 59 597L590 597H1121Z' />
    </mask>
    <path
      d='M1121 597C1121 456.17 1065.06 321.108 965.474 221.526C865.892 121.944 730.83 66 590 66C449.17 66 314.108 121.944 214.526 221.526C114.945 321.108 59 456.17 59 597L590 597H1121Z'
      fill='url(#paint0_linear_1_669)'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-6-inside-6_1_669)'
    />
    <rect width='35' height='3' transform='matrix(-1 0 0 1 1112 574.925)' fill='white' />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.959262 0.282518 0.282518 0.959262 1097.15 468.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.992069 0.125691 0.125691 0.992069 1109.62 538.867)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.9487 0.316179 0.316179 0.9487 1087.05 434.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.978253 0.207415 0.207415 0.978253 1104.38 501.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.919258 0.393657 0.393657 0.919258 1073.57 400.055)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.883358 0.468699 0.468699 0.883358 1056.08 365.796)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.879184 0.476482 0.476482 0.879184 1040.51 338.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.500135 0.865948 0.865948 0.500135 827.402 137.716)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.235114 0.971968 0.971968 0.235114 732.187 99.5909)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.387326 0.921943 0.921943 0.387326 798.989 123.773)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.200683 0.979656 0.979656 0.200683 697.692 91.3333)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.309648 0.950851 0.950851 0.309648 764.376 109.839)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.118866 0.99291 0.99291 0.118866 660.754 85.5684)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.0359288 0.999354 0.999354 0.0359288 622.339 79.9251)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.0271019 0.999633 0.999633 0.0271019 591.284 79.9251)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.503549 0.863966 0.863966 -0.503549 305.381 164.68)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.727122 0.686508 0.686508 -0.727122 221.276 230.884)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.608149 0.793823 0.793823 -0.608149 275.425 184.891)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.750885 0.660433 0.660433 -0.750885 196.988 256.732)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.671798 0.740734 0.740734 -0.671798 246.149 208.025)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.802998 0.595982 0.595982 -0.802998 173.65 285.939)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.849755 0.527178 0.527178 -0.849755 152.859 318.302)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.854377 0.519653 0.519653 -0.854377 137.483 345.283)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.859029 0.511926 0.511926 0.859029 1026.46 316.656)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.679406 0.733763 0.733763 0.679406 959.446 233.2)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.787872 0.615839 0.615839 0.787872 1005.96 286.898)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.653101 0.757271 0.757271 0.653101 933.363 209.164)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.734167 0.678969 0.678969 0.734167 982.546 257.849)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.588146 0.808755 0.808755 0.588146 903.931 186.111)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.518891 0.85484 0.85484 0.518891 874.436 162.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.511321 0.85939 0.85939 0.511321 851.422 148.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.0136712 0.999907 0.999907 -0.0136712 564.976 81.857)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.295606 0.95531 0.95531 -0.295606 457.788 97.8119)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.139242 0.990258 0.990258 -0.139242 528.953 84.7268)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.329119 0.944288 0.944288 -0.329119 425.648 106.912)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.220769 0.975326 0.975326 -0.220769 492.087 90.4766)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.406187 0.91379 0.91379 -0.406187 391.042 120.144)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.480732 0.876868 0.876868 -0.480732 358.544 135.367)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.488457 0.872588 0.872588 -0.488457 330.286 150.39)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.874851 0.484392 0.484392 -0.874851 125.317 368.629)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.976061 0.217498 0.217498 -0.976061 86.9609 468.556)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.928797 0.37059 0.37059 -0.928797 109.931 401.326)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.983125 0.182934 0.182934 -0.983125 79.3281 503.193)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.956296 0.292401 0.292401 -0.956296 96.625 436.186)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.994898 0.10089 0.10089 -0.994898 74.2324 540.23)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.999841 0.0178498 0.0178498 -0.999841 69.0527 577.388)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.999959 0.00901921 0.00901921 -0.999959 68.832 608.784)'
      fill='white'
    />
    <path
      d='M846.615 242.265L570.845 571.758L616.512 605.125L846.615 242.265Z'
      fill='#323232'
    />
    <circle
      cx='44.5'
      cy='44.5'
      r='49.5'
      transform='matrix(-1 0 0 1 636 547)'
      fill='#323232'
      stroke='white'
      strokeWidth='10'
    />
    <defs>
      <linearGradient
        id='paint0_linear_1_669'
        x1='590'
        y1='66'
        x2='590'
        y2='1128'
        gradientUnits='userSpaceOnUse'
      >
        <stop stopColor='#FF8888' />
        <stop offset='0.489583' stopColor='white' />
      </linearGradient>
    </defs>
  </svg>
);

export const NormalIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    width={width}
    height={width}
    viewBox='0 0 1182 1182'
    fill='none'
    xmlns='http://www.w3.org/2000/svg'
  >
    <mask id='path-1-inside-1_23_351' fill='white'>
      <path d='M0 591C0 513.389 15.2867 436.538 44.9872 364.834C74.6877 293.131 118.22 227.979 173.1 173.1C227.979 118.22 293.131 74.6877 364.834 44.9872C436.538 15.2867 513.389 -3.39249e-06 591 0C668.611 3.3925e-06 745.463 15.2867 817.166 44.9872C888.869 74.6877 954.021 118.22 1008.9 173.1C1063.78 227.979 1107.31 293.131 1137.01 364.834C1166.71 436.538 1182 513.389 1182 591L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 513.389 15.2867 436.538 44.9872 364.834C74.6877 293.131 118.22 227.979 173.1 173.1C227.979 118.22 293.131 74.6877 364.834 44.9872C436.538 15.2867 513.389 -3.39249e-06 591 0C668.611 3.3925e-06 745.463 15.2867 817.166 44.9872C888.869 74.6877 954.021 118.22 1008.9 173.1C1063.78 227.979 1107.31 293.131 1137.01 364.834C1166.71 436.538 1182 513.389 1182 591L591 591H0Z'
      fill='#FF5656'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-1-inside-1_23_351)'
    />
    <mask id='path-2-inside-2_23_351' fill='white'>
      <path d='M0 591C0 466.08 39.583 344.372 113.066 243.351C186.549 142.329 290.152 67.1902 409.002 28.7211C527.851 -9.7481 655.834 -9.56862 774.575 29.2337C893.316 68.0361 996.708 143.466 1069.91 244.693L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 466.08 39.583 344.372 113.066 243.351C186.549 142.329 290.152 67.1902 409.002 28.7211C527.851 -9.7481 655.834 -9.56862 774.575 29.2337C893.316 68.0361 996.708 143.466 1069.91 244.693L591 591H0Z'
      fill='#FF8888'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-2-inside-2_23_351)'
    />
    <mask id='path-3-inside-3_23_351' fill='white'>
      <path d='M0 591C0 497.833 22.0263 405.987 64.2818 322.953C106.537 239.92 167.825 168.054 243.144 113.217C318.462 58.3798 405.679 22.1265 497.677 7.41475C589.674 -7.29697 683.847 -0.0503071 772.511 28.5635L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 497.833 22.0263 405.987 64.2818 322.953C106.537 239.92 167.825 168.054 243.144 113.217C318.462 58.3798 405.679 22.1265 497.677 7.41475C589.674 -7.29697 683.847 -0.0503071 772.511 28.5635L591 591H0Z'
      fill='#FEE114'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-3-inside-3_23_351)'
    />
    <mask id='path-4-inside-4_23_351' fill='white'>
      <path d='M0 591C0 466.369 39.4 344.928 112.568 244.036C185.737 143.144 288.927 67.9646 407.391 29.2449L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 466.369 39.4 344.928 112.568 244.036C185.737 143.144 288.927 67.9646 407.391 29.2449L591 591H0Z'
      fill='#D1D80F'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-4-inside-4_23_351)'
    />
    <mask id='path-5-inside-5_23_351' fill='white'>
      <path d='M0 591C0 465.799 39.7606 343.834 113.549 242.688L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 465.799 39.7606 343.834 113.549 242.688L591 591H0Z'
      fill='#84BD32'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-5-inside-5_23_351)'
    />
    <mask id='path-6-inside-6_23_351' fill='white'>
      <path d='M1123 597C1123 456.17 1067.06 321.108 967.474 221.526C867.892 121.944 732.83 66 592 66C451.17 66 316.108 121.944 216.526 221.526C116.945 321.108 61 456.17 61 597L592 597H1123Z' />
    </mask>
    <path
      d='M1123 597C1123 456.17 1067.06 321.108 967.474 221.526C867.892 121.944 732.83 66 592 66C451.17 66 316.108 121.944 216.526 221.526C116.945 321.108 61 456.17 61 597L592 597H1123Z'
      fill='url(#paint0_linear_23_351)'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-6-inside-6_23_351)'
    />
    <rect width='35' height='3' transform='matrix(-1 0 0 1 1112 574.925)' fill='white' />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.959262 0.282518 0.282518 0.959262 1097.15 468.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.992069 0.125691 0.125691 0.992069 1109.62 538.867)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.9487 0.316179 0.316179 0.9487 1087.05 434.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.978253 0.207415 0.207415 0.978253 1104.38 501.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.919258 0.393657 0.393657 0.919258 1073.57 400.055)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.883358 0.468699 0.468699 0.883358 1056.08 365.796)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.879184 0.476482 0.476482 0.879184 1040.51 338.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.500135 0.865948 0.865948 0.500135 827.402 137.716)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.235114 0.971968 0.971968 0.235114 732.187 99.5909)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.387326 0.921943 0.921943 0.387326 798.989 123.773)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.200683 0.979656 0.979656 0.200683 697.692 91.3333)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.309648 0.950851 0.950851 0.309648 764.376 109.839)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.118866 0.99291 0.99291 0.118866 660.754 85.5684)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.0359288 0.999354 0.999354 0.0359288 622.339 79.9251)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.0271019 0.999633 0.999633 0.0271019 591.284 79.9251)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.503549 0.863966 0.863966 -0.503549 305.381 164.68)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.727122 0.686508 0.686508 -0.727122 221.276 230.884)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.608149 0.793823 0.793823 -0.608149 275.425 184.891)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.750885 0.660433 0.660433 -0.750885 196.988 256.732)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.671798 0.740734 0.740734 -0.671798 246.149 208.025)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.802998 0.595982 0.595982 -0.802998 173.65 285.939)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.849755 0.527178 0.527178 -0.849755 152.859 318.302)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.854377 0.519653 0.519653 -0.854377 137.483 345.284)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.859029 0.511926 0.511926 0.859029 1026.46 316.656)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.679406 0.733763 0.733763 0.679406 959.446 233.2)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.787872 0.615839 0.615839 0.787872 1005.96 286.898)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.653101 0.757271 0.757271 0.653101 933.363 209.164)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.734167 0.678969 0.678969 0.734167 982.546 257.849)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.588146 0.808755 0.808755 0.588146 903.931 186.111)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.518891 0.85484 0.85484 0.518891 874.436 162.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.511321 0.85939 0.85939 0.511321 851.422 148.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.0136712 0.999907 0.999907 -0.0136712 564.976 81.857)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.295606 0.95531 0.95531 -0.295606 457.788 97.8119)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.139242 0.990258 0.990258 -0.139242 528.953 84.7268)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.329119 0.944288 0.944288 -0.329119 425.648 106.912)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.220769 0.975326 0.975326 -0.220769 492.087 90.4766)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.406187 0.91379 0.91379 -0.406187 391.042 120.144)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.480732 0.876868 0.876868 -0.480732 358.544 135.367)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.488457 0.872588 0.872588 -0.488457 330.286 150.39)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.874851 0.484392 0.484392 -0.874851 125.317 368.629)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.976061 0.217498 0.217498 -0.976061 86.9609 468.555)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.928797 0.37059 0.37059 -0.928797 109.931 401.326)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.983125 0.182934 0.182934 -0.983125 79.3281 503.193)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.956296 0.292401 0.292401 -0.956296 96.625 436.185)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.994898 0.10089 0.10089 -0.994898 74.2324 540.23)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.999841 0.0178498 0.0178498 -0.999841 69.0527 577.388)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.999959 0.00901921 0.00901921 -0.999959 68.832 608.784)'
      fill='white'
    />
    <path
      d='M588.654 163.187L562.835 592.08L619.393 591.755L588.654 163.187Z'
      fill='#323232'
    />
    <circle
      cx='44.5'
      cy='44.5'
      r='49.5'
      transform='matrix(-1 0 0 1 636 547)'
      fill='#323232'
      stroke='white'
      strokeWidth='10'
    />
    <defs>
      <linearGradient
        id='paint0_linear_23_351'
        x1='592'
        y1='66'
        x2='592'
        y2='1128'
        gradientUnits='userSpaceOnUse'
      >
        <stop stopColor='#FEE114' />
        <stop offset='0.489583' stopColor='white' />
      </linearGradient>
    </defs>
  </svg>
);

export const EasyIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    width={width}
    height={width}
    viewBox='0 0 1182 1182'
    fill='none'
    xmlns='http://www.w3.org/2000/svg'
  >
    <mask id='path-1-inside-1_23_351' fill='white'>
      <path d='M0 591C0 513.389 15.2867 436.538 44.9872 364.834C74.6877 293.131 118.22 227.979 173.1 173.1C227.979 118.22 293.131 74.6877 364.834 44.9872C436.538 15.2867 513.389 -3.39249e-06 591 0C668.611 3.3925e-06 745.463 15.2867 817.166 44.9872C888.869 74.6877 954.021 118.22 1008.9 173.1C1063.78 227.979 1107.31 293.131 1137.01 364.834C1166.71 436.538 1182 513.389 1182 591L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 513.389 15.2867 436.538 44.9872 364.834C74.6877 293.131 118.22 227.979 173.1 173.1C227.979 118.22 293.131 74.6877 364.834 44.9872C436.538 15.2867 513.389 -3.39249e-06 591 0C668.611 3.3925e-06 745.463 15.2867 817.166 44.9872C888.869 74.6877 954.021 118.22 1008.9 173.1C1063.78 227.979 1107.31 293.131 1137.01 364.834C1166.71 436.538 1182 513.389 1182 591L591 591H0Z'
      fill='#FF5656'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-1-inside-1_23_351)'
    />
    <mask id='path-2-inside-2_23_351' fill='white'>
      <path d='M0 591C0 466.08 39.583 344.372 113.066 243.351C186.549 142.329 290.152 67.1902 409.002 28.7211C527.851 -9.7481 655.834 -9.56862 774.575 29.2337C893.316 68.0361 996.708 143.466 1069.91 244.693L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 466.08 39.583 344.372 113.066 243.351C186.549 142.329 290.152 67.1902 409.002 28.7211C527.851 -9.7481 655.834 -9.56862 774.575 29.2337C893.316 68.0361 996.708 143.466 1069.91 244.693L591 591H0Z'
      fill='#FF8888'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-2-inside-2_23_351)'
    />
    <mask id='path-3-inside-3_23_351' fill='white'>
      <path d='M0 591C0 497.833 22.0263 405.987 64.2818 322.953C106.537 239.92 167.825 168.054 243.144 113.217C318.462 58.3798 405.679 22.1265 497.677 7.41475C589.674 -7.29697 683.847 -0.0503071 772.511 28.5635L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 497.833 22.0263 405.987 64.2818 322.953C106.537 239.92 167.825 168.054 243.144 113.217C318.462 58.3798 405.679 22.1265 497.677 7.41475C589.674 -7.29697 683.847 -0.0503071 772.511 28.5635L591 591H0Z'
      fill='#FEE114'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-3-inside-3_23_351)'
    />
    <mask id='path-4-inside-4_23_351' fill='white'>
      <path d='M0 591C0 466.369 39.4 344.928 112.568 244.036C185.737 143.144 288.927 67.9646 407.391 29.2449L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 466.369 39.4 344.928 112.568 244.036C185.737 143.144 288.927 67.9646 407.391 29.2449L591 591H0Z'
      fill='#D1D80F'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-4-inside-4_23_351)'
    />
    <mask id='path-5-inside-5_23_351' fill='white'>
      <path d='M0 591C0 465.799 39.7606 343.834 113.549 242.688L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 465.799 39.7606 343.834 113.549 242.688L591 591H0Z'
      fill='#84BD32'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-5-inside-5_23_351)'
    />
    <mask id='path-6-inside-6_23_351' fill='white'>
      <path d='M1121 597C1121 456.17 1065.06 321.108 965.474 221.526C865.892 121.944 730.83 66 590 66C449.17 66 314.108 121.944 214.526 221.526C114.945 321.108 59 456.17 59 597L590 597H1121Z' />
    </mask>
    <path
      d='M1121 597C1121 456.17 1065.06 321.108 965.474 221.526C865.892 121.944 730.83 66 590 66C449.17 66 314.108 121.944 214.526 221.526C114.945 321.108 59 456.17 59 597L590 597H1121Z'
      fill='url(#paint0_linear_23_351)'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-6-inside-6_23_351)'
    />
    <rect width='35' height='3' transform='matrix(-1 0 0 1 1112 574.925)' fill='white' />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.959262 0.282518 0.282518 0.959262 1097.15 468.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.992069 0.125691 0.125691 0.992069 1109.62 538.867)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.9487 0.316179 0.316179 0.9487 1087.05 434.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.978253 0.207415 0.207415 0.978253 1104.38 501.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.919258 0.393657 0.393657 0.919258 1073.57 400.055)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.883358 0.468699 0.468699 0.883358 1056.08 365.796)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.879184 0.476482 0.476482 0.879184 1040.51 338.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.500135 0.865948 0.865948 0.500135 827.402 137.716)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.235114 0.971968 0.971968 0.235114 732.187 99.5909)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.387326 0.921943 0.921943 0.387326 798.989 123.773)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.200683 0.979656 0.979656 0.200683 697.692 91.3333)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.309648 0.950851 0.950851 0.309648 764.376 109.839)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.118866 0.99291 0.99291 0.118866 660.754 85.5684)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.0359288 0.999354 0.999354 0.0359288 622.339 79.9251)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.0271019 0.999633 0.999633 0.0271019 591.284 79.9251)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.503549 0.863966 0.863966 -0.503549 305.381 164.68)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.727122 0.686508 0.686508 -0.727122 221.276 230.884)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.608149 0.793823 0.793823 -0.608149 275.425 184.891)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.750885 0.660433 0.660433 -0.750885 196.988 256.732)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.671798 0.740734 0.740734 -0.671798 246.149 208.025)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.802998 0.595982 0.595982 -0.802998 173.65 285.939)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.849755 0.527178 0.527178 -0.849755 152.859 318.302)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.854377 0.519653 0.519653 -0.854377 137.483 345.283)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.859029 0.511926 0.511926 0.859029 1026.46 316.656)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.679406 0.733763 0.733763 0.679406 959.446 233.2)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.787872 0.615839 0.615839 0.787872 1005.96 286.898)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.653101 0.757271 0.757271 0.653101 933.363 209.164)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.734167 0.678969 0.678969 0.734167 982.546 257.849)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.588146 0.808755 0.808755 0.588146 903.931 186.111)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.518891 0.85484 0.85484 0.518891 874.436 162.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.511321 0.85939 0.85939 0.511321 851.422 148.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.0136712 0.999907 0.999907 -0.0136712 564.976 81.857)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.295606 0.95531 0.95531 -0.295606 457.788 97.8119)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.139242 0.990258 0.990258 -0.139242 528.953 84.7268)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.329119 0.944288 0.944288 -0.329119 425.648 106.912)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.220769 0.975326 0.975326 -0.220769 492.087 90.4766)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.406187 0.91379 0.91379 -0.406187 391.042 120.144)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.480732 0.876868 0.876868 -0.480732 358.544 135.367)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.488457 0.872588 0.872588 -0.488457 330.286 150.39)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.874851 0.484392 0.484392 -0.874851 125.317 368.629)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.976061 0.217498 0.217498 -0.976061 86.9609 468.556)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.928797 0.37059 0.37059 -0.928797 109.931 401.326)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.983125 0.182934 0.182934 -0.983125 79.3281 503.193)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.956296 0.292401 0.292401 -0.956296 96.625 436.186)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.994898 0.10089 0.10089 -0.994898 74.2324 540.23)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.999841 0.0178498 0.0178498 -0.999841 69.0527 577.388)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.999959 0.00901921 0.00901921 -0.999959 68.832 608.784)'
      fill='white'
    />
    <path
      d='M320.827 252.242L565.435 605.487L609.714 570.298L320.827 252.242Z'
      fill='#323232'
    />
    <circle
      cx='44.5'
      cy='44.5'
      r='49.5'
      transform='matrix(-1 0 0 1 636 547)'
      fill='#323232'
      stroke='white'
      strokeWidth='10'
    />
    <defs>
      <linearGradient
        id='paint0_linear_23_351'
        x1='590'
        y1='66'
        x2='590'
        y2='1128'
        gradientUnits='userSpaceOnUse'
      >
        <stop stopColor='#D1D80F' />
        <stop offset='0.489583' stopColor='white' />
      </linearGradient>
    </defs>
  </svg>
);

export const VeryEasyIcon = ({ width, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    width={width}
    height={width}
    viewBox='0 0 1182 1182'
    fill='none'
    xmlns='http://www.w3.org/2000/svg'
  >
    <mask id='path-1-inside-1_23_351' fill='white'>
      <path d='M0 591C0 513.389 15.2867 436.538 44.9872 364.834C74.6877 293.131 118.22 227.979 173.1 173.1C227.979 118.22 293.131 74.6877 364.834 44.9872C436.538 15.2867 513.389 -3.39249e-06 591 0C668.611 3.3925e-06 745.463 15.2867 817.166 44.9872C888.869 74.6877 954.021 118.22 1008.9 173.1C1063.78 227.979 1107.31 293.131 1137.01 364.834C1166.71 436.538 1182 513.389 1182 591L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 513.389 15.2867 436.538 44.9872 364.834C74.6877 293.131 118.22 227.979 173.1 173.1C227.979 118.22 293.131 74.6877 364.834 44.9872C436.538 15.2867 513.389 -3.39249e-06 591 0C668.611 3.3925e-06 745.463 15.2867 817.166 44.9872C888.869 74.6877 954.021 118.22 1008.9 173.1C1063.78 227.979 1107.31 293.131 1137.01 364.834C1166.71 436.538 1182 513.389 1182 591L591 591H0Z'
      fill='#FF5656'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-1-inside-1_23_351)'
    />
    <mask id='path-2-inside-2_23_351' fill='white'>
      <path d='M0 591C0 466.08 39.583 344.372 113.066 243.351C186.549 142.329 290.152 67.1902 409.002 28.7211C527.851 -9.7481 655.834 -9.56862 774.575 29.2337C893.316 68.0361 996.708 143.466 1069.91 244.693L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 466.08 39.583 344.372 113.066 243.351C186.549 142.329 290.152 67.1902 409.002 28.7211C527.851 -9.7481 655.834 -9.56862 774.575 29.2337C893.316 68.0361 996.708 143.466 1069.91 244.693L591 591H0Z'
      fill='#FF8888'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-2-inside-2_23_351)'
    />
    <mask id='path-3-inside-3_23_351' fill='white'>
      <path d='M0 591C0 497.833 22.0263 405.987 64.2818 322.953C106.537 239.92 167.825 168.054 243.144 113.217C318.462 58.3798 405.679 22.1265 497.677 7.41475C589.674 -7.29697 683.847 -0.0503071 772.511 28.5635L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 497.833 22.0263 405.987 64.2818 322.953C106.537 239.92 167.825 168.054 243.144 113.217C318.462 58.3798 405.679 22.1265 497.677 7.41475C589.674 -7.29697 683.847 -0.0503071 772.511 28.5635L591 591H0Z'
      fill='#FEE114'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-3-inside-3_23_351)'
    />
    <mask id='path-4-inside-4_23_351' fill='white'>
      <path d='M0 591C0 466.369 39.4 344.928 112.568 244.036C185.737 143.144 288.927 67.9646 407.391 29.2449L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 466.369 39.4 344.928 112.568 244.036C185.737 143.144 288.927 67.9646 407.391 29.2449L591 591H0Z'
      fill='#D1D80F'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-4-inside-4_23_351)'
    />
    <mask id='path-5-inside-5_23_351' fill='white'>
      <path d='M0 591C0 465.799 39.7606 343.834 113.549 242.688L591 591H0Z' />
    </mask>
    <path
      d='M0 591C0 465.799 39.7606 343.834 113.549 242.688L591 591H0Z'
      fill='#84BD32'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-5-inside-5_23_351)'
    />
    <mask id='path-6-inside-6_23_351' fill='white'>
      <path d='M1125 597C1125 456.17 1069.06 321.108 969.474 221.526C869.892 121.944 734.83 66 594 66C453.17 66 318.108 121.944 218.526 221.526C118.945 321.108 63 456.17 63 597L594 597H1125Z' />
    </mask>
    <path
      d='M1125 597C1125 456.17 1069.06 321.108 969.474 221.526C869.892 121.944 734.83 66 594 66C453.17 66 318.108 121.944 218.526 221.526C118.945 321.108 63 456.17 63 597L594 597H1125Z'
      fill='url(#paint0_linear_23_351)'
      stroke='white'
      strokeWidth='30'
      mask='url(#path-6-inside-6_23_351)'
    />
    <rect width='35' height='3' transform='matrix(-1 0 0 1 1112 574.925)' fill='white' />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.959262 0.282518 0.282518 0.959262 1097.15 468.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.992069 0.125691 0.125691 0.992069 1109.62 538.867)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.9487 0.316179 0.316179 0.9487 1087.05 434.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.978253 0.207415 0.207415 0.978253 1104.38 501.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.919258 0.393657 0.393657 0.919258 1073.57 400.055)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.883358 0.468699 0.468699 0.883358 1056.08 365.796)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.879184 0.476482 0.476482 0.879184 1040.51 338.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.500135 0.865948 0.865948 0.500135 827.402 137.716)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.235114 0.971968 0.971968 0.235114 732.187 99.5909)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.387326 0.921943 0.921943 0.387326 798.989 123.773)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.200683 0.979656 0.979656 0.200683 697.692 91.3333)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.309648 0.950851 0.950851 0.309648 764.376 109.839)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.118866 0.99291 0.99291 0.118866 660.754 85.5684)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.0359288 0.999354 0.999354 0.0359288 622.339 79.9251)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.0271019 0.999633 0.999633 0.0271019 591.284 79.9251)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.503549 0.863966 0.863966 -0.503549 305.381 164.68)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.727122 0.686508 0.686508 -0.727122 221.276 230.884)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.608149 0.793823 0.793823 -0.608149 275.425 184.891)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.750885 0.660433 0.660433 -0.750885 196.988 256.732)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.671798 0.740734 0.740734 -0.671798 246.149 208.025)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.802998 0.595982 0.595982 -0.802998 173.65 285.939)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.849755 0.527178 0.527178 -0.849755 152.859 318.302)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.854377 0.519653 0.519653 -0.854377 137.483 345.283)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.859029 0.511926 0.511926 0.859029 1026.46 316.656)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.679406 0.733763 0.733763 0.679406 959.446 233.2)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.787872 0.615839 0.615839 0.787872 1005.96 286.898)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.653101 0.757271 0.757271 0.653101 933.363 209.164)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.734167 0.678969 0.678969 0.734167 982.546 257.849)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.588146 0.808755 0.808755 0.588146 903.931 186.111)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.518891 0.85484 0.85484 0.518891 874.436 162.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(-0.511321 0.85939 0.85939 0.511321 851.422 148.925)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.0136712 0.999907 0.999907 -0.0136712 564.976 81.857)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.295606 0.95531 0.95531 -0.295606 457.788 97.8119)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.139242 0.990258 0.990258 -0.139242 528.953 84.7268)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.329119 0.944288 0.944288 -0.329119 425.648 106.912)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.220769 0.975326 0.975326 -0.220769 492.087 90.4766)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.406187 0.91379 0.91379 -0.406187 391.042 120.144)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.480732 0.876868 0.876868 -0.480732 358.544 135.367)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.488457 0.872588 0.872588 -0.488457 330.286 150.39)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.874851 0.484392 0.484392 -0.874851 125.317 368.629)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.976061 0.217498 0.217498 -0.976061 86.9609 468.556)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.928797 0.37059 0.37059 -0.928797 109.931 401.326)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.983125 0.182934 0.182934 -0.983125 79.3281 503.193)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.956296 0.292401 0.292401 -0.956296 96.625 436.186)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.994898 0.10089 0.10089 -0.994898 74.2324 540.23)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.999841 0.0178498 0.0178498 -0.999841 69.0527 577.388)'
      fill='white'
    />
    <rect
      width='35'
      height='3'
      transform='matrix(0.999959 0.00901921 0.00901921 -0.999959 68.832 608.784)'
      fill='white'
    />
    <path
      d='M178.567 438.481L571.869 611.48L591.185 558.321L178.567 438.481Z'
      fill='#323232'
    />
    <circle
      cx='44.5'
      cy='44.5'
      r='49.5'
      transform='matrix(-1 0 0 1 636 547)'
      fill='#323232'
      stroke='white'
      strokeWidth='10'
    />
    <defs>
      <linearGradient
        id='paint0_linear_23_351'
        x1='594'
        y1='66'
        x2='594'
        y2='1128'
        gradientUnits='userSpaceOnUse'
      >
        <stop stopColor='#84BD32' />
        <stop offset='0.489583' stopColor='white' />
      </linearGradient>
    </defs>
  </svg>
);
