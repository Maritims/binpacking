import {PerspectiveCamera, Object3D} from "three";
import {OrbitControls} from "three/addons";
import * as THREE from "three";

const ZOOM_SPEED_FACTOR = 0.05;

/**
 * @type {Point3D}
 */
let _startingPosition;
/**
 * @type {PerspectiveCamera & Object3D}
 */
let _camera;
/**
 * @type {OrbitControls}
 */
let _controls;

/**
 * @param {HTMLCanvasElement} canvas
 */
function createCameraControls(canvas) {
    _controls = new OrbitControls(_camera, canvas);

    _controls.enableDamping = true;
    _controls.dampingFactor = 0.05;
    _controls.minDistance = 2;
    _controls.maxDistance = 1000;
    _controls.target.set(0, 0, 0);

    return _controls;
}

/**
 * @param {Point3D} startingPosition
 * @param {HTMLCanvasElement} canvas
 * @return {PerspectiveCamera & Object3D}
 */
export function createCamera(startingPosition, canvas) {
    _startingPosition = startingPosition;

    _camera = new PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
    const {x: cameraStartX, y: cameraStartY, z: cameraStartZ} = startingPosition;
    _camera.position.set(cameraStartX, cameraStartY, cameraStartZ);
    _camera.lookAt(0, 0, 0);

    createCameraControls(canvas);

    return _camera;
}

export function updateCameraControls() {
    _controls.update();
}

export function resetCameraView() {
    _camera.position.set(_startingPosition.x, _startingPosition.y, _startingPosition.z);
    _controls.target.set(0, 0, 0);
    _controls.saveState();
    _controls.reset();
}

/**
 * @param {number} deltaY
 */
export function adjustCameraZoom(deltaY) {
    const scale = _camera.position.z * ZOOM_SPEED_FACTOR;
    const direction = new THREE.Vector3();
    _camera.getWorldDirection(direction);

    const zoomAmount = deltaY * scale * 0.002;
    _camera.position.addScaledVector(direction, -zoomAmount);
}

export function updateCameraAspect() {
    _camera.aspect = window.innerWidth / window.innerHeight;
    _camera.updateProjectionMatrix();
}