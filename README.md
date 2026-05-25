# binpacking

This library uses binpacking to pack items into bins in 1D, 2D and 3D.

## Supported algorithms

### 1D

- First-fit-decreasing bin packing.

### 2D

- First-fit-decreasing shelf packing.

### 3D

- First-fit-decreasing layer packing with custom constraints, such as forcing new bin allocation when the next item breaches a certain length or combination of length and girth.

## Visualisation suite

This project has a Jetty-based backend and a Three.js-based frontend which can visualise elements to aid in the development process.

The frontend subscribes to Server Sent Events (SSE) from the backend. The backend can receive payloads via HTTP POST for broadcasting via SSE to all subscribing clients.

Whenever you run the unit tests in `no.clueless.binpacking.three_dimensional.NextFitLayerPackerTest`, the resulting bins are serialized and posted to the backend for broadcasting.

### Features

- Camera zoom, pan, rotation and reset.
- Visualisation of the current packing with colorization of the individual items within the different bins.
- Shrinking bins to visualise optimised packing.

### Start the visualisation suite

Start the suite by running the following commands:

```shell
mvn test -Pvisualize -Dtest=NextFitLayerPackerVisualizationTest
npm i --prefix src/test/resources/visualization
npm run dev --prefix src/test/resources/visualization
```