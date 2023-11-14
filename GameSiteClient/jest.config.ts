
const config: any = {
    preset: "jest-preset-angular",
    reporters: [ "default", "jest-junit" ],
    setupFilesAfterEnv: ["<rootDir>/src/test-setup.ts"],
};

export default config;