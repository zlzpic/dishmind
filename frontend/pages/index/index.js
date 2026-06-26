const { request } = require('../../utils/request');
const { getDifficultyText, getPlaceholderColor } = require('../../utils/util');

Page({
  data: {
    activeTab: 'hot',
    recipes: [],
    page: 1,
    size: 10,
    hasMore: true,
    loading: false,
    quickTags: [
      { id: 1, name: '辣味', icon: '🌶️', color: '#ff6b6b' },
      { id: 15, name: '鸡肉', icon: '🍗', color: '#feca57' },
      { id: 25, name: '快手', icon: '⚡', color: '#48dbfb' },
      { id: 24, name: '早餐', icon: '🥪', color: '#ff9ff3' }
    ]
  },

  onLoad() {
    this.loadRecipes();
  },

  onShow() {
    // 返回时刷新推荐
    if (this.data.activeTab === 'recommend') {
      this.refresh();
    }
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    if (tab === this.data.activeTab) return;
    
    this.setData({ activeTab: tab, recipes: [], page: 1, hasMore: true });
    this.loadRecipes();
  },

 // 修改 loadRecipes 中的数据处理
async loadRecipes() {
  if (this.data.loading || !this.data.hasMore) return;
  
  this.setData({ loading: true });
  
  try {
    let url = '';
    const { activeTab, page, size } = this.data;
    
    if (activeTab === 'hot') {
      url = `/recipe/list?page=${page}&size=${size}&sort=hot`;
    } else if (activeTab === 'latest') {
      url = `/recipe/list?page=${page}&size=${size}&sort=latest`;
    } else {
      const userId = wx.getStorageSync('userId');
      if (!userId) {
        wx.showToast({ title: '请先登录', icon: 'none' });
        this.setData({ loading: false });
        return;
      }
      url = `/recommend/for-you?limit=${size}`;
    }

    const res = await request({ url });
    
    // 处理两种可能的返回格式
    let list = [];
    let total = 0;
    
    if (Array.isArray(res)) {
      // 猜你喜欢直接返回数组
      list = res;
      total = res.length;
    } else if (res.list) {
      // 分页接口返回 {list: [...], total: n}
      list = res.list;
      total = res.total || 0;
    } else {
      list = [];
    }
    
    const formatted = list.map(item => ({
      ...item,
      difficultyText: getDifficultyText(item.difficulty),
      placeholderColor: getPlaceholderColor(item.coverImage || item.title)
    }));

    this.setData({
      recipes: [...this.data.recipes, ...formatted],
      page: page + 1,
      hasMore: list.length === size && activeTab !== 'recommend'
    });
  } catch (e) {
    console.error('加载失败', e);
  } finally {
    this.setData({ loading: false });
  }
},

  onReachBottom() {
    this.loadRecipes();
  },

  refresh() {
    this.setData({ recipes: [], page: 1, hasMore: true });
    this.loadRecipes();
  },

  // goDetail(e) {
  //   // 兼容组件事件(e.detail)和普通元素事件(e.currentTarget.dataset)
  //   const id = e.detail?.id || e.currentTarget?.dataset?.id;
    
  //   if (!id || isNaN(parseInt(id))) {
  //     console.error('无效的菜品ID:', id);
  //     wx.showToast({ title: '页面跳转失败', icon: 'none' });
  //     return;
  //   }
    
  //   wx.navigateTo({ url: `/pages/recipe/detail/detail?id=${id}` });
  // }
  goDetail(e) {
    // 检查登录状态
    const userId = wx.getStorageSync('userId');
    if (!userId) {
      wx.showToast({ title: '请先登录查看', icon: 'none' });
      setTimeout(() => {
        wx.navigateTo({ url: '/pages/login/login' });
      }, 1500);
      return;
    }
    
    // 获取 ID（兼容组件事件和普通事件）
    const id = e.detail?.id || e.currentTarget?.dataset?.id;
    
    // 严格校验
    if (!id || id === 'undefined' || isNaN(parseInt(id))) {
      console.error('无效的菜谱ID:', id, '事件对象:', e);
      return; // 静默返回，不提示用户（避免误报）
    }
    
    const recipeId = parseInt(id);
    if (recipeId <= 0) {
      console.error('菜谱ID必须大于0:', recipeId);
      return;
    }
    
    wx.navigateTo({ url: `/pages/recipe/detail/detail?id=${recipeId}` });
  },

  goSearch() {
    wx.navigateTo({ url: '/pages/search/search' });
  },

  goFilter(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/category/category?tagId=${id}` });
  }
});