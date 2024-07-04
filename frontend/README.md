# Frontend


## Start the application

Run `npx nx serve frontend` to start the development server. Happy coding!

## Build for production

Run `npx nx build frontend` to build the application. The build artifacts are stored in the output directory (e.g. `dist/` or `build/`), ready to be deployed.

## Running tasks

To execute tasks with Nx use the following syntax:

```
npx nx <target> <project> <...options>
```

You can also run multiple targets:

```
npx nx run-many -t <target1> <target2>
```

..or add `-p` to filter specific projects

```
npx nx run-many -t <target1> <target2> -p <proj1> <proj2>
```

Targets can be defined in the `package.json` or `projects.json`. Learn more [in the docs](https://nx.dev/features/run-tasks).

## Explore the project graph

Run `npx nx graph` to show the graph of the workspace.
It will show tasks that you can run with Nx.

- [Learn more about Exploring the Project Graph](https://nx.dev/core-features/explore-graph)

## Run REST API Generators

1. `java -jar ./tools/swagger-codegen/swagger-codegen-cli-3.0.57.jar generate -i ../backend/services/gamesite-backend/openapi.json -l typescript-angular -o ./lib/api-sdk/src/lib/sdk --additional-properties useOverride=true`

2. Delete non-ts files
3. Fix typescript errors

## New Library

`npx nx g @nx/angular:library {name} --directory lib/{directory} --projectNameAndRootFormat as-provided`
