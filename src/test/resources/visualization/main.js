import {
    start3DEngine
} from "./engine.js";
import {resetCameraView} from "./camera.js";
import {render, shrinkWireframeToContent} from "./renderer.js";

/**
 * @type {Point3D}
 */
const cameraStartingPosition = { x:100, y:100, z: 650};
start3DEngine(cameraStartingPosition);

document.querySelector('#reset-cam').addEventListener('click', () => resetCameraView());
document.querySelector('#shrink-btn').addEventListener('click', () => shrinkWireframeToContent());

const eventSource = new EventSource("http://localhost:8081/stream");
eventSource.onmessage = (event) => {
    /**
     * @type {Bin3D[]}
     */
    const bins = JSON.parse(event.data);
    render(bins);
};