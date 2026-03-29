{
  "startPoint": {
    "x": 21.3,
    "y": 122.2,
    "heading": "constant",
    "startDeg": 135,
    "endDeg": 135,
    "locked": false
  },
  "lines": [
    {
      "id": "blue-close-1",
      "name": "ShootInitial",
      "endPoint": {
        "x": 51.0,
        "y": 92.0,
        "heading": "constant",
        "degrees": 135
      },
      "controlPoints": [],
      "color": "#6655D5"
    },
    {
      "id": "blue-close-2",
      "name": "AlignOne",
      "endPoint": {
        "x": 51.049,
        "y": 83.293,
        "heading": "constant",
        "degrees": 180
      },
      "controlPoints": [],
      "color": "#C86AA8"
    },
    {
      "id": "blue-close-3",
      "name": "CollectOne",
      "endPoint": {
        "x": 19.1,
        "y": 82.765,
        "heading": "constant",
        "degrees": 180
      },
      "controlPoints": [],
      "color": "#AAA5D7"
    },
    {
      "id": "blue-close-4",
      "name": "ShootOne",
      "endPoint": {
        "x": 51.0,
        "y": 92.0,
        "heading": "constant",
        "degrees": 140
      },
      "controlPoints": [],
      "color": "#667B97"
    },
    {
      "id": "blue-close-5",
      "name": "AlignTwo",
      "endPoint": {
        "x": 51.031,
        "y": 59.776,
        "heading": "linear",
        "startDeg": 140,
        "endDeg": 180
      },
      "controlPoints": [],
      "color": "#67C5D8"
    },
    {
      "id": "blue-close-6",
      "name": "CollectTwo",
      "endPoint": {
        "x": 17.537,
        "y": 59.723,
        "heading": "constant",
        "degrees": 180
      },
      "controlPoints": [],
      "color": "#AAA5D7"
    },
    {
      "id": "blue-close-7",
      "name": "ShootTwo",
      "endPoint": {
        "x": 51.0,
        "y": 92.0,
        "heading": "constant",
        "degrees": 140
      },
      "controlPoints": [],
      "color": "#667B97"
    },
    {
      "id": "blue-close-8",
      "name": "Leave",
      "endPoint": {
        "x": 29.159,
        "y": 70.206,
        "heading": "constant",
        "degrees": 0
      },
      "controlPoints": [],
      "color": "#FF5733"
    }
  ],
  "shapes": [],
  "sequence": [
    { "kind": "path", "lineId": "blue-close-1" },
    { "kind": "path", "lineId": "blue-close-2" },
    { "kind": "path", "lineId": "blue-close-3" },
    { "kind": "path", "lineId": "blue-close-4" },
    { "kind": "path", "lineId": "blue-close-5" },
    { "kind": "path", "lineId": "blue-close-6" },
    { "kind": "path", "lineId": "blue-close-7" },
    { "kind": "path", "lineId": "blue-close-8" }
  ],
  "settings": {
    "xVelocity": 75,
    "yVelocity": 65,
    "aVelocity": 3.14159,
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