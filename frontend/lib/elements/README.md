# elements

This library was generated with [Nx](https://nx.dev).

## Running unit tests

Run `nx test elements` to execute the unit tests.

### making components

`npx nx run elements:generate-component --name={name} --directory={directory} --selector={selector}`

Example: `npx nx run elements:generate-component --name="GsButton" --directory="button" --selector="button"`
- ts class name is `GsButton`
- directory is in `lib/elements/button`
- selector is `<gs-button>`
