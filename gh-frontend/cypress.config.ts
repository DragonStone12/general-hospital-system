import { defineConfig } from "cypress";
// import codeCoverageTask from "@cypress/code-coverage/task";

export default defineConfig({
  e2e: {
    baseUrl: "http://localhost:4200",
    reporter: 'cypress-multi-reporters',
    reporterOptions: {
      configFile: "reporter.config.json",
    },
    // setupNodeEvents(on, config) {
    //   codeCoverageTask(on, config);
    //   return config;
    // },
  },

  component: {
    devServer: {
      framework: "angular",
      bundler: "webpack",
    },
    specPattern: ['**/*.cy.ts'],
  },
});
