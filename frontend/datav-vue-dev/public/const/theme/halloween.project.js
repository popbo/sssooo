var theme_macarons = {
  "version": 1,
  "themeName": "macarons",
  "theme": {
    "seriesCnt": "4",
    "backgroundColor": "rgba(0,0,0,0)",
    "titleColor": "#008acd",
    "subtitleColor": "#aaaaaa",
    "textColorShow": false,
    "textColor": "#333",
    "markTextColor": "#eeeeee",
    "color": [
      "#2ec7c9",
      "#b6a2de",
      "#5ab1ef",
      "#ffb980",
      "#d87a80",
      "#8d98b3",
      "#e5cf0d",
      "#97b552",
      "#95706d",
      "#dc69aa",
      "#07a2a4",
      "#9a7fd1",
      "#588dd5",
      "#f5994e",
      "#c05050",
      "#59678c",
      "#c9ab00",
      "#7eb00a",
      "#6f5553",
      "#c14089"
    ],
    "borderColor": "#ccc",
    "borderWidth": 0,
    "visualMapColor": [
      "#5ab1ef",
      "#e0ffff"
    ],
    "legendTextColor": "#333333",
    "kColor": "#d87a80",
    "kColor0": "#2ec7c9",
    "kBorderColor": "#d87a80",
    "kBorderColor0": "#2ec7c9",
    "kBorderWidth": 1,
    "lineWidth": 2,
    "symbolSize": 3,
    "symbol": "emptyCircle",
    "symbolBorderWidth": 1,
    "lineSmooth": true,
    "graphLineWidth": 1,
    "graphLineColor": "#aaaaaa",
    "mapLabelColor": "#d87a80",
    "mapLabelColorE": "rgb(100,0,0)",
    "mapBorderColor": "#eeeeee",
    "mapBorderColorE": "#444444",
    "mapBorderWidth": 0.5,
    "mapBorderWidthE": 1,
    "mapAreaColor": "#dddddd",
    "mapAreaColorE": "rgba(254,153,78,1)",
    "axes": [{
        "type": "all",
        "name": "通用坐标轴",
        "axisLineShow": true,
        "axisLineColor": "#eeeeee",
        "axisTickShow": true,
        "axisTickColor": "#eeeeee",
        "axisLabelShow": true,
        "axisLabelColor": "#eeeeee",
        "splitLineShow": true,
        "splitLineColor": [
          "#aaaaaa"
        ],
        "splitAreaShow": false,
        "splitAreaColor": [
          "#eeeeee"
        ]
      },
      {
        "type": "category",
        "name": "类目坐标轴",
        "axisLineShow": true,
        "axisLineColor": "#008acd",
        "axisTickShow": true,
        "axisTickColor": "#333",
        "axisLabelShow": true,
        "axisLabelColor": "#333",
        "splitLineShow": false,
        "splitLineColor": [
          "#eee"
        ],
        "splitAreaShow": false,
        "splitAreaColor": [
          "rgba(250,250,250,0.3)",
          "rgba(200,200,200,0.3)"
        ]
      },
      {
        "type": "value",
        "name": "数值坐标轴",
        "axisLineShow": true,
        "axisLineColor": "#008acd",
        "axisTickShow": true,
        "axisTickColor": "#333",
        "axisLabelShow": true,
        "axisLabelColor": "#333",
        "splitLineShow": true,
        "splitLineColor": [
          "#eee"
        ],
        "splitAreaShow": true,
        "splitAreaColor": [
          "rgba(250,250,250,0.3)",
          "rgba(200,200,200,0.3)"
        ]
      },
      {
        "type": "log",
        "name": "对数坐标轴",
        "axisLineShow": true,
        "axisLineColor": "#008acd",
        "axisTickShow": true,
        "axisTickColor": "#333",
        "axisLabelShow": true,
        "axisLabelColor": "#333",
        "splitLineShow": true,
        "splitLineColor": [
          "#eee"
        ],
        "splitAreaShow": true,
        "splitAreaColor": [
          "rgba(250,250,250,0.3)",
          "rgba(200,200,200,0.3)"
        ]
      },
      {
        "type": "time",
        "name": "时间坐标轴",
        "axisLineShow": true,
        "axisLineColor": "#008acd",
        "axisTickShow": true,
        "axisTickColor": "#333",
        "axisLabelShow": true,
        "axisLabelColor": "#333",
        "splitLineShow": true,
        "splitLineColor": [
          "#eee"
        ],
        "splitAreaShow": false,
        "splitAreaColor": [
          "rgba(250,250,250,0.3)",
          "rgba(200,200,200,0.3)"
        ]
      }
    ],
    "axisSeperateSetting": true,
    "toolboxColor": "#2ec7c9",
    "toolboxEmpasisColor": "#18a4a6",
    "tooltipAxisColor": "#008acd",
    "tooltipAxisWidth": "1",
    "timelineLineColor": "#008acd",
    "timelineLineWidth": 1,
    "timelineItemColor": "#008acd",
    "timelineItemColorE": "#a9334c",
    "timelineCheckColor": "#2ec7c9",
    "timelineCheckBorderColor": "rgba(46,199,201,0.4)",
    "timelineItemBorderWidth": 1,
    "timelineControlColor": "#008acd",
    "timelineControlBorderColor": "#008acd",
    "timelineControlBorderWidth": 0.5,
    "timelineLabelColor": "#008acd",
    "datazoomBackgroundColor": "rgba(47,69,84,0)",
    "datazoomDataColor": "rgba(239,239,255,1)",
    "datazoomFillColor": "rgba(182,162,222,0.2)",
    "datazoomHandleColor": "#008acd",
    "datazoomHandleWidth": "100",
    "datazoomLabelColor": "#333333"
  }
}

window.echarts.registerTheme(theme_macarons.themeName, theme_macarons.theme)