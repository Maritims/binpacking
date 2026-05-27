import {
    AmbientLight,
    DirectionalLight,
    Color,
    GridHelper,
    Group,
    PerspectiveCamera,
    Scene,
    Vector3,
    WebGLRenderer, Raycaster, Vector2
} from "three";
import {OrbitControls} from "three/addons";

const ZOOM_SPEED_FACTOR = 0.05;

/**
 * @typedef {Object} EngineState
 * @property {Bin3D[]} currentBins
 * @property {Group} currentRenderedElements
 */

/**
 * @type {EngineState}
 */
export let state = {
    currentRenderedElements: new Group(),
    currentBins: []
};

/**
 * @param {Point3D} startingPosition
 * @return {PerspectiveCamera}
 */
function createCamera(startingPosition) {
    const camera = new PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
    camera.position.set(startingPosition.x, startingPosition.y, startingPosition.z);
    camera.lookAt(0, 0, 0);
    return camera;
}

/**
 * @param {PerspectiveCamera} camera
 * @param {HTMLCanvasElement} canvas
 * @return {OrbitControls}
 */
function createControls(camera, canvas) {
    const controls = new OrbitControls(camera, canvas);
    controls.enableDamping = true;
    controls.dampingFactor = 0.05;
    controls.minDistance = 2;
    controls.maxDistance = 1000;
    controls.target.set(0, 0, 0);

    return controls;
}

/**
 * @param {HTMLCanvasElement} canvas
 * @return {WebGLRenderer}
 */
function createRenderer(canvas) {
    const renderer = new WebGLRenderer({
        canvas: canvas,
        antialias: true
    });
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));

    return renderer;
}

/**
 * @param {Color} color
 * @return {Scene}
 */
function createScene(color = new Color(0x1e1e1e)) {
    const scene = new Scene();
    scene.background = color;
    scene.add(new AmbientLight(0xffffff, 0.5));

    const directionalLight = new DirectionalLight(0xffffff, 1);
    directionalLight.position.set(10, 20, 10);
    scene.add(directionalLight);

    const grid = new GridHelper(1000, 30, 0x444444, 0x2c2c2c);
    grid.position.y = -0.5;
    scene.add(grid);
    scene.add(state.currentRenderedElements);

    return scene;
}

/**
 * @param {Point3D} cameraStartingPosition The camera starting position.
 */
export function createEngine(cameraStartingPosition) {
    /**
     * The canvas to draw on.
     * @type {HTMLCanvasElement}
     */
    const canvas = document.querySelector("#bg");
    const camera = createCamera(cameraStartingPosition);
    const controls = createControls(camera, canvas);
    const renderer = createRenderer(canvas);
    const scene = createScene();
    const raycaster = new Raycaster();
    const mouse = new Vector2();

    let highlightedObject = null; // <-- Defined here!
    let originalColor = new Color();
    const HIGHLIGHT_COLOR = 0x3ae374;

    canvas.addEventListener("click", (event) => {
        const rect = canvas.getBoundingClientRect();
        mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
        mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;

        raycaster.setFromCamera(mouse, camera);
        const intersects = raycaster.intersectObjects(state.currentRenderedElements.children, true);

        const panel = document.querySelector("#selected-bin-panel");
        const idValueSpan = document.querySelector("#bin-id-val");
        const itemsContainer = document.querySelector("#bin-items-container");

        const clearHighlight = () => {
            if (highlightedObject && highlightedObject.material) {
                highlightedObject.material.color.copy(originalColor);
                highlightedObject = null;
            }
        };

        if (intersects.length > 0) {
            const clickedObject = intersects[0].object;
            const binId = clickedObject.userData.binId;

            if (binId !== undefined) {
                if (highlightedObject !== clickedObject) {
                    clearHighlight();

                    highlightedObject = clickedObject;
                    if (clickedObject.material && clickedObject.material.color) {
                        originalColor.copy(clickedObject.material.color);
                        clickedObject.material.color.setHex(HIGHLIGHT_COLOR);
                    }
                }

                // Update Bin Main Display ID
                idValueSpan.textContent = binId;

                // Find the matching bin object data in our state array
                const targetBin = state.currentBins.find(b => b.id === binId);

                // Clear out old element markup string entries
                itemsContainer.innerHTML = "";

                if (targetBin && targetBin.items && targetBin.items.length > 0) {
                    // Map items layout into custom innerHTML templates
                    targetBin.items.forEach(item => {
                        const itemElement = document.createElement("div");
                        itemElement.className = "item-node";
                        itemElement.innerHTML = `
                            <div class="item-title">${item.id || 'Unnamed Item'}</div>
                            <div class="item-details">Size: ${item.width}x${item.height}x${item.depth}</div>
                            <div class="item-details">Pos: [${item.x}, ${item.y}, ${item.z}]</div>
                        `;
                        itemsContainer.appendChild(itemElement);
                    });
                } else {
                    itemsContainer.innerHTML = `<div style="font-size:12px; color:#a4b0be; font-style:italic;">No items inside.</div>`;
                }

                panel.classList.add("visible");
            } else {
                clearHighlight();
                panel.classList.remove("visible");
            }
        } else {
            clearHighlight();
            panel.classList.remove("visible");
        }
    });

    canvas.addEventListener("wheel", (event) => {
        event.preventDefault();

        const scale = camera.position.z * ZOOM_SPEED_FACTOR;
        const direction = new Vector3();
        camera.getWorldDirection(direction);
        camera.position.addScaledVector(direction, -(event.deltaY * scale * 0.002));
    }, {passive: false});

    window.addEventListener('resize', () => {
        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();
        renderer.setSize(window.innerWidth, window.innerHeight);
        renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    });

    function animate() {
        requestAnimationFrame(animate);
        controls.update();
        renderer.render(scene, camera);
    }

    animate();

    return {
        camera,
        controls
    }
}
