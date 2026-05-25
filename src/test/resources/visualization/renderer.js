import * as THREE from 'three';

/**
 * @type {THREE.Scene}
 * @private
 */
let _scene;

/**
 * @type {THREE.WebGLRenderer}
 * @private
 */
let _renderer;

/**
 * @type {THREE.Group & THREE.Object3D}
 * @private
 */
let _currentRenderedElements;

/**
 * Keeps track of whether the layout is currently optimized/shrunk.
 * @type {boolean}
 * @private
 */
let _isShrunk = false;

/**
 * @type {Bin3D[]}
 * @private
 */
let _cachedBins = [];

/**
 * @param {Size3D} size
 * @param {Point3D} position
 * @param {THREE.Color} color
 * @return {THREE.Group & THREE.Object3D}
 */
function createBox(size, position, color = new THREE.Color(0x00ff88)) {
    const {width, height, depth} = size;
    const {x, y, z} = position;
    const geometry = new THREE.BoxGeometry(width, height, depth);
    const material = new THREE.MeshPhongMaterial({
        color: color,
        flatShading: true,
        shininess: 30
    });
    const mesh = new THREE.Mesh(geometry, material);

    const edgeGeometry = new THREE.EdgesGeometry(geometry);
    const lineMaterial = new THREE.LineBasicMaterial({color: 0x000000});
    const borderLines = new THREE.LineSegments(edgeGeometry, lineMaterial);

    const group = new THREE.Group();
    group.add(mesh);
    group.add(borderLines);
    group.position.set(x + width / 2, y + height / 2, z + depth / 2);
    return group;
}

/**
 * @param {Size3D} size
 * @param {Point3D} offset
 * @param {boolean} isShrunk
 * @return {THREE.Object3D}
 */
function createWireframe(size, offset, isShrunk) {
    const {width, height, depth} = size;
    const {x: offsetX, y: offsetY, z: offsetZ} = offset;

    const geometry = new THREE.BoxGeometry(width, height, depth);
    const edges = new THREE.EdgesGeometry(geometry);
    const wireframeColor = isShrunk ? 0x00ffcc : 0xff4757;
    const wireframe = new THREE.LineSegments(edges, new THREE.LineBasicMaterial({color: wireframeColor, linewidth: 2}));

    wireframe.position.set(
        offsetX + width / 2,
        offsetY + height / 2,
        offsetZ + depth / 2
    );

    return wireframe;
}

/**
 * @param {Bin3D} bin
 * @param {boolean} isShrunk
 * @return {THREE.Group & THREE.Object3D}
 */
function createBinGroup(bin, isShrunk = false) {
    const group = new THREE.Group();
    const wireframe = createWireframe(bin.bounds, {x: 0, y: 0, z: 0}, isShrunk);
    group.add(wireframe);

    bin.items.forEach((item, i) => {
        /**
         * @type {Size3D}
         */
        const size = {width: item.width, height: item.height, depth: item.depth};
        /**
         * @type {Point3D}
         */
        const position = {x: item.x, y: item.y, z: item.z};
        const hue = (i * 137.5) % 360;
        const color = new THREE.Color(`hsl(${hue}, 75%, 50%)`);
        const box = createBox(size, position, color);

        group.add(box);
    });

    return group;
}

/**
 * @param {HTMLCanvasElement} canvas
 * @return {THREE.WebGLRenderer}
 */
export function createRenderer(canvas) {
    _renderer = new THREE.WebGLRenderer({
        canvas: canvas,
        antialias: true
    });
    _renderer.setSize(window.innerWidth, window.innerHeight);
    _renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));

    return _renderer;
}

/**
 * @param {THREE.PerspectiveCamera} camera
 */
export function updateRenderer(camera) {
    _renderer.render(_scene, camera);
}

/**
 * @param {THREE.Color} color
 * @return {THREE.Scene}
 */
export function createScene(color = new THREE.Color(0x1e1e1e)) {
    _scene = new THREE.Scene();
    _scene.background = color;
    _scene.add(new THREE.AmbientLight(0xffffff, 0.5));

    const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
    directionalLight.position.set(10, 20, 10);
    _scene.add(directionalLight);

    const grid = new THREE.GridHelper(1000, 30, 0x444444, 0x2c2c2c);
    grid.position.y = -0.5;
    _scene.add(grid);

    _currentRenderedElements = new THREE.Group();
    _scene.add(_currentRenderedElements);

    return _scene;
}

export function updateRendererSizeAndRatio() {
    _renderer.setSize(window.innerWidth, window.innerHeight);
    _renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
}

/**
 * Renders all the bins to the scene.
 * @param {Bin3D[]} bins
 */
export function render(bins) {
    _cachedBins = bins;

    while(_currentRenderedElements.children.length > 0) {
        _currentRenderedElements.remove(_currentRenderedElements.children[0]);
    }

    let currentXOffset = 0;
    const BIN_SPACING = 50;

    bins.forEach(bin => {
        const group = createBinGroup(bin);
        group.position.set(currentXOffset, 0, 0);
        _currentRenderedElements.add(group);

        currentXOffset += bin.bounds.width + BIN_SPACING;
    });
}

export function shrinkWireframeToContent() {
    _isShrunk = !_isShrunk;

    let currentXOffset = 0;
    const BIN_SPACING = 50;

    while (_currentRenderedElements.children.length > 0) {
        _currentRenderedElements.remove(_currentRenderedElements.children[0]);
    }

    _cachedBins.forEach(bin => {
        let minX = Infinity, minY = Infinity, minZ = Infinity;
        let maxX = -Infinity, maxY = -Infinity, maxZ = -Infinity;

        // 1. Calculate tight spatial boundaries inside local bin coordinates
        bin.items.forEach(item => {
            minX = Math.min(minX, item.x);
            minY = Math.min(minY, item.y);
            minZ = Math.min(minZ, item.z);

            maxX = Math.max(maxX, item.x + item.width);
            maxY = Math.max(maxY, item.y + item.height);
            maxZ = Math.max(maxZ, item.z + item.depth);
        });

        /**
         * @type {THREE.Group & THREE.Object3D}
         */
        let binGroup;

        if (_isShrunk && minX !== Infinity) {
            // Build a modified, tight structural representation for the container envelope
            /**
             * @type {Bin3D}
             */
            const tightBounds = {
                bounds: {
                    width: maxX - minX,
                    height: maxY - minY,
                    depth: maxZ - minZ
                },
                // Shift item positions backwards by minX/Y/Z adjustments so they line up with the shrunk box corner
                items: bin.items.map(item => ({
                    width: item.width,
                    height: item.height,
                    depth: item.depth,
                    x: item.x - minX,
                    y: item.y - minY,
                    z: item.z - minZ
                }))
            };

            binGroup = createBinGroup(tightBounds, true);
            // Apply world-space positioning plus local offset shifts
            binGroup.position.set(currentXOffset + minX, minY, minZ);
        } else {
            // Fallback default: render the original full bounds matching the bin limits
            binGroup = createBinGroup(bin, false);
            binGroup.position.set(currentXOffset, 0, 0);
        }

        _currentRenderedElements.add(binGroup);
        currentXOffset += bin.bounds.width + BIN_SPACING;
    });
}