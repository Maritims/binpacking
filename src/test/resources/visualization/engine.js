import {
    updateCameraAspect,
    adjustCameraZoom,
    createCamera,
    updateCameraControls
} from "./camera.js";
import {createRenderer, createScene, updateRenderer, updateRendererSizeAndRatio} from "./renderer.js";

/**
 * Initializes the 3D engine and starts the animation loop.
 * @param {Point3D} cameraStartingPosition The camera starting position.
 */
export function start3DEngine(cameraStartingPosition) {
    /**
     * The canvas to draw on.
     * @type {HTMLCanvasElement}
     */
    const canvas = document.querySelector("#bg");
    const camera = createCamera(cameraStartingPosition, canvas);

    createRenderer(canvas);
    createScene();

    canvas.addEventListener("wheel", (event) => {
        event.preventDefault();
        adjustCameraZoom(event.deltaY);
    }, {passive: false});

    window.addEventListener('resize', () => {
        updateCameraAspect();
        updateRendererSizeAndRatio();
    });

    function animate() {
        requestAnimationFrame(animate);
        updateCameraControls();
        updateRenderer(camera);
    }

    animate();
}
