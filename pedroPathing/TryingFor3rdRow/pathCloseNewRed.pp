{
  "startPoint": {
    "x": 122.696,
    "y": 122.293,
    "heading": "constant",
    "startDeg": 45,
    "endDeg": 45,
    "locked": false
  },
  "lines": [
    {
      "id": "red-v2-1",
      "name": "ShootOneInitial",
      "endPoint": {
        "x": 91.888,
        "y": 91.626,
        "heading": "constant",
        "degrees": 45
      },
      "controlPoints": [],
      "color": "#6655D5"
    },
    {
      "id": "red-v2-2",
      "name": "AlignOne",
      "endPoint": {
        "x": 91.645,
        "y": 59.766,
        "heading": "linear",
        "startDeg": 45,
        "endDeg": 0
      },
      "controlPoints": [],
      "color": "#C86AA8"
    },
    {
      "id": "red-v2-3",
      "name": "CollectOne",
      "endPoint": {
        "x": 124.972,
        "y": 59.766,
        "heading": "constant",
        "degrees": 0
      },
      "controlPoints": [],
      "color": "#AAA5D7"
    },
    {
      "id": "red-v2-4",
      "name": "TapGate",
      "endPoint": {
        "x": 126.654,
        "y": 71.056,
        "heading": "constant",
        "degrees": 180
      },
      "controlPoints": [],
      "color": "#FF5733"
    },
    {
      "id": "red-v2-5",
      "name": "ShootOne",
      "endPoint": {
        "x": 91.888,
        "y": 91.626,
        "heading": "linear",
        "startDeg": 180,
        "endDeg": 45
      },
      "controlPoints": [],
      "color": "#667B97"
    },
    {
      "id": "red-v2-6",
      "name": "AlignTwo",
      "endPoint": {
        "x": 91.944,
        "y": 83.533,
        "heading": "linear",
        "startDeg": 45,
        "endDeg": 0
      },
      "controlPoints": [],
      "color": "#67C5D8"
    },
    {
      "id": "red-v2-7",
      "name": "CollectTwo",
      "endPoint": {
        "x": 125.327,
        "y": 83.533,
        "heading": "constant",
        "degrees": 0
      },
      "controlPoints": [],
      "color": "#AAA5D7"
    },
    {
      "id": "red-v2-8",
      "name": "ShootTwo",
      "endPoint": {
        "x": 91.888,
        "y": 91.626,
        "heading": "constant",
        "degrees": 45
      },
      "controlPoints": [],
      "color": "#667B97"
    },
    {
      "id": "red-v2-9",
      "name": "AlignThree",
      "endPoint": {
        "x": 91.234,
        "y": 35.813,
        "heading": "linear",
        "startDeg": 45,
        "endDeg": 0
      },
      "controlPoints": [],
      "color": "#67C5D8"
    },
    {
      "id": "red-v2-10",
      "name": "CollectThree",
      "endPoint": {
        "x": 125.243,
        "y": 35.813,
        "heading": "constant",
        "degrees": 0
      },
      "controlPoints": [],
      "color": "#AAA5D7"
    },
    {
      "id": "red-v2-11",
      "name": "ShootThree",
      "endPoint": {
        "x": 91.888,
        "y": 91.626,
        "heading": "constant",
        "degrees": 45
      },
      "controlPoints": [],
      "color": "#667B97"
    },
    {
      "id": "red-v2-12",
      "name": "Leave",
      "endPoint": {
        "x": 95.841,
        "y": 71.869,
        "heading": "linear",
        "startDeg": 0,
        "endDeg": 45
      },
      "controlPoints": [],
      "color": "#67C5D8"
    }
  ],
  "shapes": [],
  "sequence": [
    { "kind": "path", "lineId": "red-v2-1" },
    { "kind": "path", "lineId": "red-v2-2" },
    { "kind": "path", "lineId": "red-v2-3" },
    { "kind": "path", "lineId": "red-v2-4" },
    { "kind": "path", "lineId": "red-v2-5" },
    { "kind": "path", "lineId": "red-v2-6" },
    { "kind": "path", "lineId": "red-v2-7" },
    { "kind": "path", "lineId": "red-v2-8" },
    { "kind": "path", "lineId": "red-v2-9" },
    { "kind": "path", "lineId": "red-v2-10" },
    { "kind": "path", "lineId": "red-v2-11" },
    { "kind": "path", "lineId": "red-v2-12" }
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
    "fieldMap": "decode.webp"
  },
  "version": "1.2.1"
}