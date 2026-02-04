module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true,
    node: true
  },
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-essential',
    '@vue/eslint-config-standard'
  ],
  parserOptions: {
    ecmaVersion: 2021,
    parser: '@babel/eslint-parser',
    sourceType: 'module',
    requireConfigFile: false,
    babelOptions: {
      presets: ['@babel/preset-env']
    }
  },
  plugins: [
    'vue'
  ],
  rules: {
    // Vue相关规则
    'vue/multi-word-component-names': 'off',
    'vue/no-unused-vars': 'error',
    'vue/require-default-prop': 'off',
    'vue/html-self-closing': 'off',
    'vue/max-attributes-per-line': 'off',
    
    // JavaScript相关规则
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'semi': ['error', 'never'],
    'comma-dangle': ['error', 'never'],
    'quotes': ['error', 'single'],
    'indent': ['error', 2],
    'space-before-function-paren': ['error', 'always'],
    'keyword-spacing': ['error', { before: true, after: true }],
    'eol-last': ['error', 'always'],
    'no-trailing-spaces': 'error',
    'object-curly-spacing': ['error', 'always'],
    'array-bracket-spacing': ['error', 'never'],
    
    // 变量相关规则
    'no-unused-vars': 'error',
    'no-undef': 'error',
    
    // 代码风格规则
    'prefer-const': 'error',
    'no-var': 'error',
    'prefer-arrow-callback': 'error',
    'func-style': ['error', 'expression'],
    
    // 错误预防规则
    'no-duplicate-imports': 'error',
    'no-implicit-coercion': 'error',
    'no-multi-spaces': 'error',
    'no-multiple-empty-lines': ['error', { max: 1, maxEOF: 0 }]
  },
  overrides: [
    {
      files: ['*.vue'],
      rules: {
        'vue/script-indent': ['error', 2, { baseIndent: 1 }],
        'vue/html-indent': ['error', 2],
        'vue/max-attributes-per-line': 'off'
      }
    }
  ]
}