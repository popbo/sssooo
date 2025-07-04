const diff = 5;

const isIntersectToTop = (from, to) => {
  return (
    Math.abs(to.top - from.intimeTop) < diff ||
    Math.abs(to.top + to.component.height - from.intimeTop) < diff ||
    Math.abs(to.top + to.component.height / 2 - from.intimeTop) < diff
  );
};

const isIntersectToBottom = (from, to) => {
  return (
    Math.abs(to.top - from.intimeTop - from.component.height) < diff ||
    Math.abs(to.top + to.component.height - from.intimeTop - from.component.height) < diff ||
    Math.abs(to.top + to.component.height / 2 - from.intimeTop - from.component.height) < diff
  );
};

const isIntersectToLeft = (from, to) => {
  return (
    Math.abs(to.left - from.intimeLeft) < diff ||
    Math.abs(to.left + to.component.width - from.intimeLeft) < diff ||
    Math.abs(to.left + to.component.width / 2 - from.intimeLeft) < diff
  );
};

const isIntersectToRight = (from, to) => {
  return (
    Math.abs(to.left - from.intimeLeft - from.component.width) < diff ||
    Math.abs(to.left + to.component.width - from.intimeLeft - from.component.width) < diff ||
    Math.abs(to.left + to.component.width / 2 - from.intimeLeft - from.component.width) < diff
  );
};

const isIntersectToVertical = (from, to) => {
  return (
    Math.abs(to.left - from.intimeLeft - from.component.width / 2) < diff ||
    Math.abs(to.left + to.component.width - from.intimeLeft - from.component.width / 2) < diff ||
    Math.abs(to.left + to.component.width / 2 - from.intimeLeft - from.component.width / 2) < diff
  );
};

const isIntersectToHorizontal = (from, to) => {
  return (
    Math.abs(to.top - from.intimeTop - from.component.height / 2) < diff ||
    Math.abs(to.top + to.component.height - from.intimeTop - from.component.height / 2) < diff ||
    Math.abs(to.top + to.component.height / 2 - from.intimeTop - from.component.height / 2) < diff
  );
};
export default {
  isIntersectToTop,
  isIntersectToBottom,
  isIntersectToLeft,
  isIntersectToRight,
  isIntersectToVertical,
  isIntersectToHorizontal,
};
