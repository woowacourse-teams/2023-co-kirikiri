const fs = require('fs');
const path = require('path');

const checkDirectory = (dir) => {
  fs.readdir(dir, (err, files) => {
    if (err) throw err;

    files.forEach((file) => {
      const filePath = path.join(dir, file);
      fs.stat(filePath, (err, stats) => {
        if (err) throw err;
        if (stats.isDirectory()) {
          checkDirectory(filePath);
        } else if (filePath.endsWith('.js') || filePath.endsWith('.ts')) {
          const baseFileName = file.substring(0, file.length - 3);
          const testFile = path.join(
            process.cwd(),
            'src',
            'tests',
            'utilTests',
            `${baseFileName}.test.ts`
          );

          if (!fs.existsSync(testFile)) {
            console.error(
              `${file} <= 유틸 파일에 대한 테스트 파일이 없습니다. 서운합니다 😢\n${file.substring(
                0,
                file.length - 3
              )}.test.ts 파일을 만들어주세요.`
            );
            process.exit(1);
          }
        }
      });
    });
  });
};

const utilsDir = path.join(process.cwd(), 'src', 'utils');
checkDirectory(utilsDir);
