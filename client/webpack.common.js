const path = require('path');

const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');

require('dotenv').config();

module.exports = {
  entry: './src/index.tsx',

  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'bundle.[fullhash].js',
    assetModuleFilename: 'assets/[hash][ext][query]',
    clean: true,
  },

  resolve: {
    extensions: ['.tsx', '.ts', '.js', '.json', '...'],
    alias: {
      '@': path.resolve(__dirname, 'src'),
      '@App': path.resolve(__dirname, 'src/App.tsx'),
      '@apis': path.resolve(__dirname, 'src/apis'),
      '@assets': path.resolve(__dirname, 'src/assets'),
      '@components': path.resolve(__dirname, 'src/components'),
      '@constants': path.resolve(__dirname, 'src/constants'),
      '@hooks': path.resolve(__dirname, 'src/hooks'),
      '@mocks': path.resolve(__dirname, 'src/mocks'),
      '@pages': path.resolve(__dirname, 'src/pages'),
      '@styles': path.resolve(__dirname, 'src/styles'),
      '@myTypes': path.resolve(__dirname, 'src/myTypes'),
      '@utils': path.resolve(__dirname, 'src/utils'),
    },
  },

  module: {
    rules: [
      {
        test: /\.(ts|tsx)$/,
        exclude: /node_modules/,
        use: ['babel-loader', 'ts-loader'],
      },
      {
        test: /\.(png|jpe?g|gif|svg)$/i,
        type: 'asset/resource',
        generator: {
          filename: 'images/[hash][ext][query]',
        },
        use: [
          {
            loader: 'image-webpack-loader',
            options: {
              mozjpeg: {
                progressive: true,
                quality: 65,
              },
              optipng: {
                enabled: false,
              },
              pngquant: {
                quality: [0.65, 0.9],
                speed: 4,
              },
              gifsicle: {
                interlaced: false,
              },
              webp: {
                quality: 75,
              },
            },
          },
        ],
      },
      {
        test: /\.json$/,
        use: ['json-loader'],
        type: 'javascript/auto',
      },
      {
        test: /\.(woff(2)?|ttf|eot|svg|otf)(\?v=\d+\.\d+\.\d+)?$/,
        type: 'asset/resource',
        generator: {
          filename: 'static/fonts/[hash][ext][query]',
        },
      },
    ],
  },

  performance: {
    hints: false,
    maxEntrypointSize: 512000,
    maxAssetSize: 512000,
  },

  plugins: [
    new HtmlWebpackPlugin({
      template: './public/index.html',
      // favicon: './public/favicon.ico',
    }),
    new webpack.DefinePlugin({
      'process.env.PROD_SERVER': JSON.stringify(process.env.PROD_SERVER),
      'process.env.API_TEST_SERVER': JSON.stringify(process.env.API_TEST_SERVER),
    }),
  ],
};
