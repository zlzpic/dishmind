const { request } = require('../../utils/request');
const { getPlaceholderColor } = require('../../utils/util');

Page({
  data: {
    keyword: '',
    history: [],
    hotKeywords: ['红烧肉', '麻婆豆腐', '早餐', '快手菜', '鸡肉', '减肥餐', '川菜', '粤菜'],
    recipes: [],
    page: 1,
    size: 10,
    total: 0,
    hasMore: true,
    loading: false,
    searching: false
  },

  onLoad() {
    this.loadHistory();
  },

  loadHistory() {
    const history = wx.getStorageSync('searchHistory') || [];
    this.setData({ history });
  },

  inputKeyword(e) {
    this.setData({ keyword: e.detail.value });
  },

  clearKeyword() {
    this.setData({ 
      keyword: '', 
      searching: false, 
      recipes: [],
      page: 1
    });
  },

  goBack() {
    wx.navigateBack();
  },

  async doSearch() {
    const { keyword } = this.data;
    if (!keyword.trim()) return;

    // 保存搜索历史
    this.saveHistory(keyword);

    this.setData({ 
      searching: true, 
      recipes: [], 
      page: 1, 
      hasMore: true,
      loading: true 
    });

    await this.loadSearchResults();
  },

  saveHistory(keyword) {
    let history = wx.getStorageSync('searchHistory') || [];
    // 去重并放到最前面
    history = history.filter(item => item !== keyword);
    history.unshift(keyword);
    // 最多保存10条
    if (history.length > 10) history = history.slice(0, 10);
    wx.setStorageSync('searchHistory', history);
    this.setData({ history });
  },

  // 修改 loadSearchResults
async loadSearchResults() {
  if (this.data.loading && this.data.page > 1) return;
  
  this.setData({ loading: true });
  
  try {
    const res = await request({
      url: `/recipe/search?keyword=${encodeURIComponent(this.data.keyword)}&page=${this.data.page}&size=${this.data.size}`
    });

    const list = res.list || [];
    const total = res.total || 0;

    const formatted = list.map(item => ({
      ...item,
      placeholderColor: getPlaceholderColor(item.coverImage || item.title)
    }));

    this.setData({
      recipes: [...this.data.recipes, ...formatted],
      total: total,
      page: this.data.page + 1,
      hasMore: list.length === this.data.size
    });
  } catch (e) {
    console.error('搜索失败', e);
  } finally {
    this.setData({ loading: false });
  }
},

  searchByHistory(e) {
    const keyword = e.currentTarget.dataset.keyword;
    this.setData({ keyword });
    this.doSearch();
  },

  clearHistory() {
    wx.showModal({
      title: '提示',
      content: '确定清空搜索历史吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('searchHistory');
          this.setData({ history: [] });
        }
      }
    });
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/recipe/detail/detail?id=${id}` });
  },

  onReachBottom() {
    if (this.data.searching) {
      this.loadSearchResults();
    }
  }
});