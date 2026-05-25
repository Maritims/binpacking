# binpacking

This library uses binpacking to pack items into bins in 1D, 2D and 3D.

## Run the visualization suite

This project has a Jetty-based backend and a Three.js-based frontend which can visualize elements to aid in the development process.

The frontend subscribes to Server Sent Events (SSE) from the backend. The backend can receive payloads via HTTP POST for broadcasting via SSE to all subscribing clients.

Whenever you run the unit tests in `no.clueless.binpacking.three_dimensional.NextFitLayerPackerTest`, the resulting bins are serialized and posted to the backend for broadcasting.

Start the suite by running the following commands:

```shell
mvn test -Pvisualize -Dtest=NextFitLayerPackerVisualizationTest
npm run dev --prefix src/test/resources/visualization
```