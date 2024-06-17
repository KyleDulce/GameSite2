# api-sdk

This library was generated with [Nx](https://nx.dev).

## Running unit tests

Run `nx test api-sdk` to execute the unit tests.

## Run REST API Generators

`java -jar ./tools/swagger-codegen/swagger-codegen-cli-3.0.57.jar generate -i ../backend/services/gamesite-backend/openapi.json -l typescript-angular -o ./lib/api-sdk/src/lib/sdk`

