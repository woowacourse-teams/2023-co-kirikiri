const path = require('path');
const { merge } = require('webpack-merge');
const common = require('./webpack.common');

const ReactRefreshWebpackPlugin = require('@pmmmwh/react-refresh-webpack-plugin');

module.exports = merge(common, {
  mode: 'development',
  devtool: 'eval',

  plugins: [new ReactRefreshWebpackPlugin()],

  devServer: {
    static: path.resolve(__dirname, 'dist'),
    compress: true,
    port: 3000,
    historyApiFallback: true,
    host: 'localhost',
    open: true,
  },
});
