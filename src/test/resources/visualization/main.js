import {
    createEngine,
    state
} from "./engine.js";
import {render, shrinkWireframeToContent} from "./renderer.js";

/**
 * @type {Point3D}
 */
const cameraStartingPosition = { x:100, y:100, z: 650};
const { camera, controls} = createEngine(cameraStartingPosition);

document.querySelector('#reset-cam').addEventListener('click', () => {
    camera.position.set(cameraStartingPosition.x, cameraStartingPosition.y, cameraStartingPosition.z);
    controls.target.set(0, 0, 0);
    controls.saveState();
    controls.reset();
});
document.querySelector('#shrink-btn').addEventListener('click', () => shrinkWireframeToContent(state.currentBins, state.currentRenderedElements));

const eventSource = new EventSource("http://localhost:8081/stream");
eventSource.onmessage = (event) => {
    /**
     * @type {Bin3D[]}
     */
    state.currentBins = JSON.parse(event.data);
    render(state.currentBins, state.currentRenderedElements);
};