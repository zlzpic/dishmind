const { request } = require('../../../utils/request');
const { getAvatarUrl } = require('../../../utils/util');

Page({
  data: { 
    userInfo: null,
    stats: { 
      favorites: 0, 
      recipes: 0, 
      history: 0 
    },
    avatarUrl: '/images/avatar/default.png',  // 默认头像路径
    isLogin: false
  },
  
  onShow() {
    const userInfo = wx.getStorageSync('userInfo');
  
    
    const userId = wx.getStorageSync('userId');
    
    if (userInfo && userId) {
      // 先处理头像 URL
      const processedAvatar = getAvatarUrl(userInfo.avatarUrl);
    
      
      const processedInfo = {
        ...userInfo,
        avatarDisplayUrl: processedAvatar
      };
      
      this.setData({ 
        userInfo: processedInfo,
        avatarUrl: processedAvatar,
        isLogin: true
      });
      
      this.loadStats();
    } else {
      this.setData({ 
        userInfo: null,
        avatarUrl: '/images/avatar/default.png',
        isLogin: false,
        stats: { favorites: 0, recipes: 0, history: 0 }
      });
    }
  },
  
  // 加载统计数据
  async loadStats() {
    try {
      const userId = wx.getStorageSync('userId');
      
      // 获取收藏数（size=1 减少数据传输，取 total）
      const favRes = await request({ 
        url: '/favorite/list?page=1&size=1' 
      });
      const favCount = favRes.total || 0;
      
      // 获取我的菜谱数
      const recipeRes = await request({ 
        url: '/recipe/my?page=1&size=1' 
      });
      const recipeCount = recipeRes.total || 0;
      
      // 获取浏览历史数（从本地存储）
      const historyCount = wx.getStorageSync('viewHistory')?.length || 0;
      
      this.setData({
        'stats.favorites': favCount,
        'stats.recipes': recipeCount,
        'stats.history': historyCount
      });
      
    } catch (e) {
      console.error('加载统计失败', e);
    }
  },

  // 头像加载失败时使用默认头像
  onAvatarError() {
    console.log('头像加载失败，使用默认头像');
    this.setData({
      avatarUrl: '/images/avatar/default.png'
    });
  },

  // 点击头像/信息区域
  editProfile() {
    if (!this.data.isLogin) {
      this.goLogin();
      return;
    }
    wx.showToast({ title: '编辑功能开发中', icon: 'none' });
  },
  
  // 跳转收藏页
  goFavorites() {
    if (!this.data.isLogin) {
      wx.navigateTo({ url: '/pages/login/login' });
      return;
    }
    wx.navigateTo({ url: '/pages/user/favorites/favorites' });
  },
  
  // 跳转我的菜谱页（专门的页面）
  goMyRecipes() {
    if (!this.data.isLogin) {
      wx.navigateTo({ url: '/pages/login/login' });
      return;
    }
    wx.navigateTo({ url: '/pages/user/my-recipes/my-recipes' });
  },

  // 浏览历史（占位）
  goHistory() {
    wx.showToast({ title: '浏览历史功能开发中', icon: 'none' });
  },

  // 设置（占位）
  goSettings() {
    wx.showToast({ title: '设置功能开发中', icon: 'none' });
  },

  // 关于
  goAbout() {
    wx.showModal({
      title: '关于 DishMind',
      content: 'DishMind 是一个智能菜谱推荐平台，基于您的口味偏好为您推荐最适合的美食。\n\n版本：1.0.0',
      showCancel: false
    });
  },
  
  // 跳转登录
  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' });
  },
  
  // 退出登录
  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.clearStorage();
          this.setData({ 
            userInfo: null, 
            isLogin: false,
            avatarUrl: '/images/avatar/default.png',
            stats: { favorites: 0, recipes: 0, history: 0 }
          });
          wx.showToast({ title: '已退出', icon: 'success' });
        }
      }
    });
  }
});