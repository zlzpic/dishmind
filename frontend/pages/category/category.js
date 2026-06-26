const { request } = require('../../utils/request');
const { getPlaceholderColor } = require('../../utils/util');

Page({
  data: {
    categories: [],
    selectedTags: [],
    recipes: [],
    page: 1,
    size: 10,
    hasMore: true,
    loading: false,
    showResults: false,
    recipeCount: 0
  },

  async onLoad(options) {
    await this.loadTags();
    if (options.tagId) {
      this.autoSelectTag(parseInt(options.tagId));
    }
  },

  async loadTags() {
    try {
      const res = await request({ url: '/tag/all' });
      const categories = res.map(cat => ({
        ...cat,
        tags: cat.tags.map(t => ({ ...t, selected: false }))
      }));
      this.setData({ categories });
    } catch (e) {
      console.error('加载标签失败', e);
    }
  },

  autoSelectTag(tagId) {
    const { categories } = this.data;
    let found = false;
    
    categories.forEach((cat, catIndex) => {
      cat.tags.forEach((tag) => {
        if (tag.id === tagId && !found) {
          found = true;
          // 模拟点击该标签
          this.toggleTag({
            currentTarget: {
              dataset: { 
                catIndex: catIndex,
                tag: tag
              }
            }
          });
        }
      });
    });
  },

  toggleTag(e) {
    const { catIndex, tag } = e.currentTarget.dataset;
    
    // 检查参数有效性
    if (catIndex === undefined || !tag) {
      console.error('toggleTag: 缺少参数', { catIndex, tag });
      return;
    }

    const { categories, selectedTags } = this.data;
    
    // 深拷贝分类数组
    const newCategories = JSON.parse(JSON.stringify(categories));
    
    // 检查分类索引有效性
    if (!newCategories[catIndex] || !newCategories[catIndex].tags) {
      console.error('toggleTag: 无效的分类索引', catIndex, newCategories);
      return;
    }
    
    // 查找并切换标签选中状态
    const tagIndex = newCategories[catIndex].tags.findIndex(t => t.id === tag.id);
    if (tagIndex === -1) {
      console.error('toggleTag: 未找到标签', tag);
      return;
    }
    
    const isSelected = !newCategories[catIndex].tags[tagIndex].selected;
    newCategories[catIndex].tags[tagIndex].selected = isSelected;
    
    // 更新已选标签数组
    let newSelected = [...selectedTags];
    if (isSelected) {
      newSelected.push(tag);
    } else {
      newSelected = newSelected.filter(t => t.id !== tag.id);
    }
    
    this.setData({ 
      categories: newCategories, 
      selectedTags: newSelected,
      showResults: false
    });
    
    // 自动查询数量
    if (newSelected.length > 0) {
      this.checkCount();
    }
  },

  removeTag(e) {
    const id = e.currentTarget.dataset.id;
    const { categories, selectedTags } = this.data;
    
    // 深拷贝并取消选中
    const newCategories = categories.map(cat => ({
      ...cat,
      tags: cat.tags.map(t => t.id === id ? { ...t, selected: false } : t)
    }));
    
    this.setData({
      categories: newCategories,
      selectedTags: selectedTags.filter(t => t.id !== id),
      showResults: false
    });
  },

  clearTags() {
    const { categories } = this.data;
    const newCategories = categories.map(cat => ({
      ...cat,
      tags: cat.tags.map(t => ({ ...t, selected: false }))
    }));
    
    this.setData({
      categories: newCategories,
      selectedTags: [],
      showResults: false,
      recipes: []
    });
  },

  async checkCount() {
    try {
      const tagIds = this.data.selectedTags.map(t => t.id).join(',');
      const res = await request({
        url: `/recipe/filter?tagIds=${tagIds}&page=1&size=1`
      });
      this.setData({ recipeCount: res.total || 0 });
    } catch (e) {
      console.error(e);
    }
  },

  // 关键修复：不再提前设置 loading: true
  applyFilter() {
    if (this.data.selectedTags.length === 0) return;
    
    this.setData({ 
      showResults: true, 
      recipes: [], 
      page: 1, 
      hasMore: true
      // 注意：不要在这里设置 loading: true！
    });
    
    this.loadFilteredRecipes();
  },

  async loadFilteredRecipes() {
    // 检查是否可以加载
    if (!this.data.hasMore || this.data.loading) return;
    
    this.setData({ loading: true });
    
    try {
      const tagIds = this.data.selectedTags.map(t => t.id).join(',');
      const res = await request({
        url: `/recipe/filter?tagIds=${tagIds}&page=${this.data.page}&size=${this.data.size}`
      });
      
      // 适配新的响应格式 {list: [...], total: n}
      const list = res.list || [];
      const total = res.total || 0;
      
      const formatted = list.map(item => ({
        ...item,
        placeholderColor: getPlaceholderColor(item.coverImage || item.title)
      }));
      
      this.setData({
        recipes: [...this.data.recipes, ...formatted],
        page: this.data.page + 1,
        hasMore: list.length === this.data.size,
        recipeCount: total
      });
    } catch (e) {
      console.error('筛选失败', e);
      wx.showToast({ title: '加载失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 返回筛选界面
  backToFilter() {
    this.setData({
      showResults: false,
      recipes: [],
      page: 1,
      hasMore: true
    });
  },

  // goDetail(e) {
  //   const id = e.detail?.id || e.currentTarget?.dataset?.id;
  //   if (!id || isNaN(parseInt(id))) {
  //     wx.showToast({ title: '无效的菜谱ID', icon: 'none' });
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
    
    const id = e.detail?.id || e.currentTarget?.dataset?.id;
    
    if (!id || id === 'undefined' || isNaN(parseInt(id))) {
      console.error('无效的菜谱ID:', id, '事件对象:', e);
      return;
    }
    
    const recipeId = parseInt(id);
    wx.navigateTo({ url: `/pages/recipe/detail/detail?id=${recipeId}` });
  },

  onReachBottom() {
    if (this.data.showResults) {
      this.loadFilteredRecipes();
    }
  }
});