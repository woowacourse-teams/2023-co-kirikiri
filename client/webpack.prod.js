const path = require('path');
const { merge } = require('webpack-merge');
const common = require('./webpack.common');

const CompressionPlugin = require('compression-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');

require('dotenv').config();

module.exports = merge(common, {
  mode: 'production',

  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: '[name].[contenthash].js',
  },

  optimization: {
    minimize: true,
    minimizer: [
      new TerserPlugin({
        parallel: true,
      }),
      new OptimizeCSSAssetsPlugin({}),
    ],
  },

  plugins: [new CleanWebpackPlugin(), new CompressionPlugin()],
});
