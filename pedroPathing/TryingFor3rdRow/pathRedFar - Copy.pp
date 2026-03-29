{
  "startPoint": {
    "x": 90.2429906542056,
    "y": 8.448598130841122,
    "heading": "linear",
    "startDeg": 90,
    "endDeg": 0,
    "locked": false
  },
  "lines": [
    {
      "id": "line-89q1wvjzvab",
      "name": "ShootInitial",
      "endPoint": {
        "x": 78.803738317757,
        "y": 16.710280373831772,
        "heading": "linear",
        "startDeg": 90,
        "endDeg": 50
      },
      "controlPoints": [],
      "color": "#6655D5",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mlsl8r80-r32nq7",
      "name": "Align1",
      "endPoint": {
        "x": 102.28971962616823,
        "y": 35.56074766355139,
        "heading": "constant",
        "reverse": false,
        "degrees": 0
      },
      "controlPoints": [],
      "color": "#C86AA8",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "mlslnoyo-cymhsu",
      "name": "Collect1",
      "endPoint": {
        "x": 129.83177570093458,
        "y": 35.78504672897196,
        "heading": "tangential",
        "reverse": false,
        "startDeg": 0,
        "endDeg": 58,
        "degrees": 58
      },
      "controlPoints": [],
      "color": "#AAA5D7",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "line-li5ddjn79h",
      "endPoint": {
        "x": 78.05607476635514,
        "y": 16.598130841121485,
        "heading": "constant",
        "reverse": false,
        "degrees": 58
      },
      "controlPoints": [],
      "color": "#667B97",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "name": "Shoot2"
    },
    {
      "id": "mlsls2bp-yumg5h",
      "name": "Leave",
      "endPoint": {
        "x": 88.3644859813084,
        "y": 36.420560747663544,
        "heading": "tangential",
        "reverse": false
      },
      "controlPoints": [],
      "color": "#67C5D8",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    }
  ],
  "shapes": [
    {
      "id": "triangle-1",
      "name": "Red Goal",
      "vertices": [
        { "x": 144, "y": 70 },
        { "x": 144, "y": 144 },
        { "x": 120, "y": 144 },
        { "x": 138, "y": 119 },
        { "x": 138, "y": 70 }
      ],
      "color": "#dc2626",
      "fillColor": "#ff6b6b"
    },
    {
      "id": "triangle-2",
      "name": "Blue Goal",
      "vertices": [
        { "x": 6, "y": 119 },
        { "x": 25, "y": 144 },
        { "x": 0, "y": 144 },
        { "x": 0, "y": 70 },
        { "x": 7, "y": 70 }
      ],
      "color": "#2563eb",
      "fillColor": "#60a5fa"
    }
  ],
  "sequence": [
    { "kind": "path", "lineId": "line-89q1wvjzvab" },
    { "kind": "wait", "id": "mlsl9k6g-530glj", "name": "Wait", "durationMs": 2000, "locked": false },
    { "kind": "path", "lineId": "mlsl8r80-r32nq7" },
    { "kind": "path", "lineId": "mlslnoyo-cymhsu" },
    { "kind": "path", "lineId": "line-li5ddjn79h" },
    { "kind": "wait", "id": "mlslrth7-o014cb", "name": "Wait", "durationMs": 2000, "locked": false },
    { "kind": "path", "lineId": "mlsls2bp-yumg5h" }
  ],
  "settings": {
    "xVelocity": 75,
    "yVelocity": 65,
    "aVelocity": 3.141592653589793,
    "kFriction": 0.1,
    "rWidth": 16,
    "rHeight": 16,
    "safetyMargin": 1,
    "maxVelocity": 40,
    "maxAcceleration": 30,
    "maxDeceleration": 30,
    "fieldMap": "decode.webp",
    "robotImage": "/robot.png",
    "theme": "auto",
    "showGhostPaths": false,
    "showOnionLayers": false,
    "onionLayerSpacing": 3,
    "onionColor": "#dc2626",
    "onionNextPointOnly": false
  },
  "version": "1.2.1",
  "timestamp": "2026-02-19T00:00:00.000Z"
}