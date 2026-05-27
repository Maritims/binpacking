/**
 * @typedef {Object} Cuboid
 * @property {number} width
 * @property {number} height
 * @property {number} depth
 * @property {number} x
 * @property {number} y
 * @property {number} z
 */

/**
 * @typedef {Object} Size3D
 * @property {number} width
 * @property {number} height
 * @property {number} depth
 */

/**
 * @typedef {Object} Bin3D
 * @property {string} id
 * @property {Size3D} bounds
 * @property {Cuboid[]} items
 */

/**
 * @typedef {Object} Point3D
 * @property {number} x
 * @property {number} y
 * @property {number} z
 */

/**
 * @callback OnBinGroupClickedCallback
 * @return void
 */