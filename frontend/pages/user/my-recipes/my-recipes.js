const { request } = require('../../../utils/request');
const { getDifficultyText, getPlaceholderColor } = require('../../../utils/util');

Page({
  data: {
    allRecipes: [],
    recipes: [],
    page: 1,
    size: 100,
    hasMore: true,
    loading: false,
    activeFilter: 'all',
    totalCount: 0,
    publishedCount: 0,
    draftCount: 0,
    auditCount: 0,
    offShelfCount: 0
  },

  onLoad() {
    this.loadAllRecipes();
  },

  onShow() {
    this.refresh();
  },

  getEmptyText() {
    const map = {
      'all': '还没有发布任何菜谱',
      'published': '没有已发布的菜谱',
      'draft': '没有草稿',
      'audit': '没有审核中的菜谱',
      'offShelf': '没有已下架的菜谱'
    };
    return map[this.data.activeFilter] || '暂无数据';
  },

  async loadAllRecipes() {
    if (this.data.loading) return;
    this.setData({ loading: true });
    
    try {
      const res = await request({ 
        url: `/recipe/my?page=1&size=${this.data.size}` 
      });
      
      const list = res.list || [];
      const statusMap = {
        0: '草稿',
        1: '审核中',
        2: '已发布',
        3: '已下架'
      };

      const formatted = list.map(item => ({
        ...item,
        difficultyText: getDifficultyText(item.difficulty),
        placeholderColor: getPlaceholderColor(item.coverImage || item.title),
        statusText: statusMap[item.status] || '未知'
      }));

      this.updateStats(formatted);
      this.setData({ hasMore: false });
    } catch (e) {
      console.error('加载失败', e);
      wx.showToast({ title: '加载失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  },

  applyFilter() {
    const { activeFilter, allRecipes } = this.data;
    let filtered = [];
    
    switch(activeFilter) {
      case 'published': filtered = allRecipes.filter(r => r.status === 2); break;
      case 'draft': filtered = allRecipes.filter(r => r.status === 0); break;
      case 'audit': filtered = allRecipes.filter(r => r.status === 1); break;
      case 'offShelf': filtered = allRecipes.filter(r => r.status === 3); break;
      default: filtered = allRecipes;
    }
    
    this.setData({ recipes: filtered });
  },

  switchFilter(e) {
    const filter = e.currentTarget.dataset.filter;
    if (filter === this.data.activeFilter) return;
    this.setData({ activeFilter: filter });
    this.applyFilter();
  },

  updateStats(newAllRecipes) {
    const totalCount = newAllRecipes.length;
    const publishedCount = newAllRecipes.filter(r => r.status === 2).length;
    const draftCount = newAllRecipes.filter(r => r.status === 0).length;
    const auditCount = newAllRecipes.filter(r => r.status === 1).length;
    const offShelfCount = newAllRecipes.filter(r => r.status === 3).length;
    
    this.setData({ 
      allRecipes: newAllRecipes,
      totalCount, publishedCount, draftCount, auditCount, offShelfCount
    });
    this.applyFilter();
  },

  // ================== 状态流转操作 ==================

  // 1. 重新上架（已下架 → 审核中）
  async republishRecipe(e) {
    const { id, title } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '重新上架',
      content: `确定要重新上架"${title}"吗？提交后将进入审核状态`,
      success: async (res) => {
        if (res.confirm) {
          try {
            await request({
              url: `/recipe/${id}/republish`,
              method: 'POST'
            });
            
            wx.showToast({ title: '已提交审核', icon: 'success' });
            
            // 本地更新状态为审核中
            const newAllRecipes = this.data.allRecipes.map(r => {
              if (r.id === id) {
                return { ...r, status: 1, statusText: '审核中' };
              }
              return r;
            });
            
            this.updateStats(newAllRecipes);
            
          } catch (e) {
            wx.showToast({ title: '操作失败', icon: 'none' });
          }
        }
      }
    });
  },

  // 2. 下架（已发布 → 已下架）
  async offlineRecipe(e) {
    const { id, title } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '确认下架',
      content: `确定要下架"${title}"吗？下架后用户将无法查看`,
      confirmColor: '#ff4d4f',
      success: async (res) => {
        if (res.confirm) {
          try {
            await request({
              url: `/recipe/${id}/offline`,
              method: 'POST'
            });
            
            wx.showToast({ title: '已下架', icon: 'success' });
            
            // 本地更新状态为已下架
            const newAllRecipes = this.data.allRecipes.map(r => {
              if (r.id === id) {
                return { ...r, status: 3, statusText: '已下架' };
              }
              return r;
            });
            
            this.updateStats(newAllRecipes);
            
          } catch (e) {
            wx.showToast({ title: '操作失败', icon: 'none' });
          }
        }
      }
    });
  },

  // 3. 提交审核（草稿 → 审核中）
  async submitAudit(e) {
    const { id, title } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '提交审核',
      content: `确定要提交"${title}"进行审核吗？`,
      success: async (res) => {
        if (res.confirm) {
          try {
            // 草稿提交审核也是调用 republish 接口（或专门的 submit 接口）
            await request({
              url: `/recipe/${id}/republish`,
              method: 'POST'
            });
            
            wx.showToast({ title: '已提交审核', icon: 'success' });
            
            // 本地更新状态为审核中
            const newAllRecipes = this.data.allRecipes.map(r => {
              if (r.id === id) {
                return { ...r, status: 1, statusText: '审核中' };
              }
              return r;
            });
            
            this.updateStats(newAllRecipes);
            
          } catch (e) {
            wx.showToast({ title: '提交失败', icon: 'none' });
          }
        }
      }
    });
  },

  // 4. 撤回（审核中 → 草稿）
  async withdrawRecipe(e) {
    const { id, title } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '撤回审核',
      content: `确定要撤回"${title}"的恢复上架审核申请吗？将回到“已下架”状态，但仍可编辑`,
      success: async (res) => {
        if (res.confirm) {
          try {
            // 调用下架接口或直接修改状态（根据后端实现）
            // 这里假设下架接口可以将审核中撤回到草稿，或者需要其他接口
            await request({
              url: `/recipe/${id}/offline`,
              method: 'POST'
            });
            
            wx.showToast({ title: '已撤回', icon: 'success' });
            
            // 本地更新状态为草稿
            const newAllRecipes = this.data.allRecipes.map(r => {
              if (r.id === id) {
                return { ...r, status: 3, statusText: '已下架' };
              }
              return r;
            });
            
            this.updateStats(newAllRecipes);
            
          } catch (e) {
            wx.showToast({ title: '操作失败', icon: 'none' });
          }
        }
      }
    });
  },

  // 5. 删除（草稿/已下架可删除）
  async deleteRecipe(e) {
    const { id, title } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '确认删除',
      content: `确定要永久删除"${title}"吗？此操作不可恢复！`,
      confirmColor: '#ff4d4f',
      success: async (res) => {
        if (res.confirm) {
          try {
            // 假设有删除接口，如果没有就只做前端移除
            // await request({ url: `/recipe/${id}/delete`, method: 'POST' });
            
            wx.showToast({ title: '删除成功', icon: 'success' });
            
            const newAllRecipes = this.data.allRecipes.filter(r => r.id !== id);
            this.updateStats(newAllRecipes);
            
          } catch (e) {
            wx.showToast({ title: '删除失败', icon: 'none' });
          }
        }
      }
    });
  },

  // ================== 导航 ==================

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

  goEdit(e) {
    const id = e.detail?.id || e.currentTarget?.dataset?.id;
    if (!id || isNaN(parseInt(id))) {
      wx.showToast({ title: '无效的菜谱ID', icon: 'none' });
      return;
    }
    wx.navigateTo({ url: `/pages/recipe/edit/edit?id=${id}` });
  },

  createRecipe() {
    wx.navigateTo({ url: '/pages/recipe/edit/edit' });
  },

  refresh() {
    this.setData({ allRecipes: [], recipes: [], page: 1, hasMore: true });
    this.loadAllRecipes();
  },

  async onPullDownRefresh() {
    await this.refresh();
    wx.stopPullDownRefresh();
  }
});