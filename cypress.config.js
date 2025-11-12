const { defineConfig } = require('cypress');

module.exports = defineConfig({
  e2e: {
    baseUrl: process.env.APP_BASE_URL || 'http://localhost:8080',
    specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}',
    supportFile: false,
    defaultCommandTimeout: 8000,   // aumentar se necessário
    requestTimeout: 10000,
    retries: {
      runMode: 2,   // 2 tentativas extras no CI
      openMode: 0,
    },
    video: true,
    screenshotOnRunFailure: true,
  },
});
