import {BoxGeometry,EdgesGeometry,Color,Group,LineBasicMaterial,LineSegments,Mesh,MeshPhongMaterial,Object3D} from "three";

/**
 * Keeps track of whether the layout is currently optimized/shrunk.
 * @type {boolean}
 * @private
 */
let _isShrunk = false;

/**
 * @param {Size3D} size
 * @param {Point3D} position
 * @param {Color} color
 * @return {Group}
 */
function createBox(size, position, color = new Color(0x00ff88)) {
    const {width, height, depth} = size;
    const {x, y, z} = position;
    const geometry = new BoxGeometry(width, height, depth);
    const material = new MeshPhongMaterial({
        color: color,
        flatShading: true,
        shininess: 30
    });
    const mesh = new Mesh(geometry, material);

    const edgeGeometry = new EdgesGeometry(geometry);
    const lineMaterial = new LineBasicMaterial({color: 0x000000});
    const borderLines = new LineSegments(edgeGeometry, lineMaterial);

    const group = new Group();
    group.add(mesh);
    group.add(borderLines);
    group.position.set(x + width / 2, y + height / 2, z + depth / 2);
    return group;
}

/**
 * @param {Size3D} size
 * @param {Point3D} offset
 * @param {boolean} isShrunk
 * @return {Object3D}
 */
function createWireframe(size, offset, isShrunk) {
    const {width, height, depth} = size;
    const {x: offsetX, y: offsetY, z: offsetZ} = offset;

    const geometry = new BoxGeometry(width, height, depth);
    const edges = new EdgesGeometry(geometry);
    const wireframeColor = isShrunk ? 0x00ffcc : 0xff4757;
    const wireframe = new LineSegments(edges, new LineBasicMaterial({color: wireframeColor, linewidth: 2}));

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
 * @return {Group}
 */
function createBinGroup(bin, isShrunk = false) {
    const group = new Group();
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
        const color = new Color(`hsl(${hue}, 75%, 50%)`);
        const box = createBox(size, position, color);

        group.add(box);
    });

    return group;
}

/**
 * Renders all the bins to the scene.
 * @param {Bin3D[]} bins
 * @param {Group} currentRenderedElements
 */
export function render(bins, currentRenderedElements) {
    while(currentRenderedElements.children.length > 0) {
        currentRenderedElements.remove(currentRenderedElements.children[0]);
    }

    let currentXOffset = 0;
    const BIN_SPACING = 50;

    bins.forEach(bin => {
        const group = createBinGroup(bin);
        group.position.set(currentXOffset, 0, 0);
        currentRenderedElements.add(group);

        currentXOffset += bin.bounds.width + BIN_SPACING;
    });
}

/**
 * @param {Bin3D[]} bins
 * @param {Group} currentRenderedElements
 */
export function shrinkWireframeToContent(bins, currentRenderedElements) {
    _isShrunk = !_isShrunk;

    let currentXOffset = 0;
    const BIN_SPACING = 50;

    while (currentRenderedElements.children.length > 0) {
        currentRenderedElements.remove(currentRenderedElements.children[0]);
    }

    bins.forEach(bin => {
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
         * @type {Group}
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

        currentRenderedElements.add(binGroup);
        currentXOffset += bin.bounds.width + BIN_SPACING;
    });
}