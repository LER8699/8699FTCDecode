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
      "id": "red-line-1",
      "name": "ShootInitial",
      "endPoint": {
        "x": 93.000,
        "y": 92.000,
        "heading": "constant",
        "degrees": 45
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
      "id": "red-line-2",
      "name": "AlignOne",
      "endPoint": {
        "x": 92.951,
        "y": 83.293,
        "heading": "constant",
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
      "id": "red-line-3",
      "name": "CollectOne",
      "endPoint": {
        "x": 124.9,
        "y": 82.765,
        "heading": "constant",
        "degrees": 0
      },
      "controlPoints": [],
      "color": "#AAA5D7",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "red-line-4",
      "name": "ShootOne",
      "endPoint": {
        "x": 93.000,
        "y": 92.000,
        "heading": "constant",
        "degrees": 40
      },
      "controlPoints": [],
      "color": "#667B97",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "red-line-5",
      "name": "AlignTwo",
      "endPoint": {
        "x": 92.969,
        "y": 59.776,
        "heading": "linear",
        "startDeg": 40,
        "endDeg": 0
      },
      "controlPoints": [],
      "color": "#67C5D8",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "red-line-6",
      "name": "CollectTwo",
      "endPoint": {
        "x": 126.463,
        "y": 59.723,
        "heading": "constant",
        "degrees": 0
      },
      "controlPoints": [],
      "color": "#AAA5D7",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "red-line-7",
      "name": "ShootTwo",
      "endPoint": {
        "x": 93.000,
        "y": 92.000,
        "heading": "constant",
        "degrees": 40
      },
      "controlPoints": [],
      "color": "#667B97",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "red-line-8",
      "name": "Leave",
      "endPoint": {
        "x": 106.142,
        "y": 77.863,
        "heading": "tangential"
      },
      "controlPoints": [],
      "color": "#67C5D8",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    }
  ],
  "shapes": [],
  "sequence": [
    { "kind": "path", "lineId": "red-line-1" },
    { "kind": "path", "lineId": "red-line-2" },
    { "kind": "path", "lineId": "red-line-3" },
    { "kind": "path", "lineId": "red-line-4" },
    { "kind": "path", "lineId": "red-line-5" },
    { "kind": "path", "lineId": "red-line-6" },
    { "kind": "path", "lineId": "red-line-7" },
    { "kind": "path", "lineId": "red-line-8" }
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