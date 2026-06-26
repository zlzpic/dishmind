const { request } = require('./request');

let viewStartTime = 0;
let currentRecipeId = null;

// 开始计时
const startViewTimer = (recipeId) => {
  viewStartTime = Date.now();
  currentRecipeId = recipeId;
  
  // 立即上报一次 VIEW（duration=0）
  reportBehavior('VIEW', 0);
};

// 结束计时并上报（5的倍数向上取整）
const endViewTimer = () => {
  if (!viewStartTime || !currentRecipeId) return;
  
  const duration = Math.ceil((Date.now() - viewStartTime) / 1000 / 5) * 5;
  if (duration > 0) {
    reportBehavior('VIEW', duration);
  }
  
  viewStartTime = 0;
  currentRecipeId = null;
};

// 上报行为
const reportBehavior = (type, durationSeconds = 0, recipeId = null) => {
  const userId = wx.getStorageSync('userId');
  if (!userId) return;
  
  const rid = recipeId || currentRecipeId;
  if (!rid) return;

  request({
    url: '/behavior/report',
    method: 'POST',
    data: {
      userId,
      recipeId: rid,
      behaviorType: type,
      durationSeconds
    }
  }).catch(() => {
    // 失败不提示，避免打扰用户
    console.log('行为上报失败');
  });
};

module.exports = {
  startViewTimer,
  endViewTimer,
  reportBehavior
};