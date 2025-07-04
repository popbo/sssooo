type DicOptionProps = {
  readonly label: string;
  readonly value: string;
  readonly disabled?: boolean;
};
interface dicOptions {
  showPos: DicOptionProps[];
  closeMethod: DicOptionProps[];
  textLean: DicOptionProps[];
  textBold: DicOptionProps[];
  textLevel: DicOptionProps[];
  textVertical:DicOptionProps[];
  borderStyle: DicOptionProps[];
}
export const dicOption:dicOptions = {
  showPos:[
    {
      label: '固定位置',
      value: 'static',
    },
    {
      label: '跟随鼠标',
      value: 'flow',
      // disabled:true
    }
  ],
  closeMethod:[
    {
      label: '默认',
      value: 'default',
    },
    {
      label: '点击空白隐藏',
      value: 'blankHide',
    },
    {
      label: '点击关闭按钮',
      value: 'closeBtn',
    },
    {
      label: '定时关闭',
      value: 'timeClose',
      disabled:true
    },
  ],
  textLean:[
    {
      label: '正常',
      value: 'normal',
    },
    {
      label: '倾斜',
      value: 'italic',
    },
  ],
  textBold:[
    {
      label: '正常',
      value: 'normal',
    },
    {
      label: '加粗',
      value: 'bold',
    },
  ],
  textLevel:[
    {
      label: '左对齐',
      value: 'flex-start',
    },
    {
      label: '居中',
      value: 'center',
    },
    {
      label: '右对齐',
      value: 'flex-end',
    },
  ],
  textVertical:[
    {
      label: '顶部对齐',
      value: 'flex-start',
    },
    {
      label: '居中',
      value: 'center',
    },
    {
      label: '底部对齐',
      value: 'flex-end',
    },
  ],
  borderStyle:[
    {
      value: 'dashed',
      label: '------',
    },
    {
      value: 'dotted',
      label: '......',
    },
    {
      value: 'solid',
      label: '——————',
    },
  ]
}