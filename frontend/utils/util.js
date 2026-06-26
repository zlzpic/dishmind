// 格式化时间（秒 -> 分:秒）
const formatDuration = (seconds) => {
  if (!seconds) return '';
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  if (mins > 0) {
    return `${mins}分${secs > 0 ? secs + '秒' : ''}`;
  }
  return `${secs}秒`;
};

// 难度文本
const getDifficultyText = (level) => {
  const map = { 1: '新手', 2: '简单', 3: '中等', 4: '困难', 5: '挑战' };
  return map[level] || '未知';
};

// 生成占位图颜色（根据字符串）
const getPlaceholderColor = (str) => {
  const colors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57', '#ff9ff3', '#54a0ff'];
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash);
  }
  return colors[Math.abs(hash) % colors.length];
};
// 在原有基础上添加头像处理函数
// const getAvatarUrl = (avatarUrl) => {
//   // 如果没有头像，返回默认头像
//   if (!avatarUrl) {
//     return '/images/avatar/default.png'; // 默认头像路径
//   }
  
//   // 如果已经是完整 URL（http/https），直接使用
//   if (avatarUrl.startsWith('http://') || avatarUrl.startsWith('https://')) {
//     return avatarUrl;
//   }
  
//   // 否则映射到本地 images/avatar/ 文件夹
//   // 例如：avatar_1 -> /images/avatar/avatar_1.png
//   return `/images/avatar/${avatarUrl}.png`;
// };
const getAvatarUrl = (avatarUrl) => {
  // 如果没有头像，返回默认头像（确保以 / 开头）
  if (!avatarUrl) {
    return '/images/avatar/default.png';
  }
  
  // 如果已经是完整 URL，直接使用
  if (avatarUrl.startsWith('http://') || avatarUrl.startsWith('https://')) {
    return avatarUrl;
  }
  
  // 关键：必须以 / 开头，表示项目根目录
  // 例如：avatat_1 -> /images/avatar/avatat_1.png
  return `/images/avatar/${avatarUrl}.png`;
};

module.exports = {
  formatDuration,
  getDifficultyText,
  getPlaceholderColor,
  getAvatarUrl  // 新增导出
};
