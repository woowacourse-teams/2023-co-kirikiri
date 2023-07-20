export const getCookie = (name: string) => {
  const cookieArr = document.cookie.split('; ');

  for (let i = 0; i < cookieArr.length; i++) {
    const cookiePair = cookieArr[i].split('=');
    if (name === cookiePair[0]) {
      return decodeURIComponent(cookiePair[1]);
    }
  }

  return null;
};

export const setCookie = (name: string, value: string, days = 1) => {
  const date = new Date();
  date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
  const expires = days ? `; expires=${date.toUTCString()}` : '';
  document.cookie = `${name}=${value || ''}${expires}; path=/`;
};

export const deleteCookie = (name: string) => {
  document.cookie = `${name}=; Max-Age=-99999999;`;
};
