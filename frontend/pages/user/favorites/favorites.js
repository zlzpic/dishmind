const { request } = require('../../../utils/request');
const { getPlaceholderColor } = require('../../../utils/util');

Page({
  data: {
    folders: [],
    currentFolder: '默认收藏夹',
    recipes: [],
    page: 1,
    size: 10,
    hasMore: true,
    loading: false,
    
    // 新建文件夹
    showCreateModal: false,
    newFolderName: '',
    
    // 移动相关
    showMoveModal: false,
    targetFolder: '',
    movingRecipeId: null,
    movingRecipeTitle: ''
  },

  onLoad() {
    this.loadFolders();
    this.loadFavorites();
  },

  onShow() {
    this.refresh();
  },

  async loadFolders() {
    try {
      const res = await request({ url: '/favorite/folders' });
      this.setData({ folders: res || ['默认收藏夹'] });
    } catch (e) {
      console.error('加载文件夹失败', e);
    }
  },

  async loadFavorites() {
    if (this.data.loading || !this.data.hasMore) return;
    
    this.setData({ loading: true });
    
    try {
      const res = await request({
        url: `/favorite/list-by-folder?folderName=${encodeURIComponent(this.data.currentFolder)}&page=${this.data.page}&size=${this.data.size}`
      });
      
      const list = (res.list || []).map(item => ({
        ...item,
        placeholderColor: getPlaceholderColor(item.coverImage || item.title)
      }));
      
      this.setData({
        recipes: [...this.data.recipes, ...list],
        page: this.data.page + 1,
        hasMore: list.length === this.data.size
      });
    } catch (e) {
      console.error(e);
    } finally {
      this.setData({ loading: false });
    }
  },

  switchFolder(e) {
    const folder = e.currentTarget.dataset.folder;
    if (folder === this.data.currentFolder) return;
    
    this.setData({
      currentFolder: folder,
      recipes: [],
      page: 1,
      hasMore: true
    });
    this.loadFavorites();
  },

  // ================== 点击菜品跳转（关键修复） ==================
  
  goDetail(e) {
    // 调试：查看事件对象
    console.log('点击事件:', e);
    console.log('detail:', e.detail);
    console.log('dataset:', e.currentTarget?.dataset);
    
    const id = e.detail?.id || e.currentTarget?.dataset?.id;
    
    if (!id || isNaN(parseInt(id))) {
      console.error('无效的菜谱ID:', id);
      wx.showToast({ title: '页面跳转失败', icon: 'none' });
      return;
    }
    
    wx.navigateTo({ url: `/pages/recipe/detail/detail?id=${id}` });
  },

  // ================== 移动功能 ==================
  
  moveFolder(e) {
    const { id, title } = e.currentTarget.dataset;
    
    if (this.data.folders.length <= 1) {
      wx.showModal({
        title: '提示',
        content: '当前只有一个收藏夹，是否创建新文件夹？',
        success: (res) => {
          if (res.confirm) {
            this.showCreateFolder();
          }
        }
      });
      return;
    }
    
    this.setData({
      showMoveModal: true,
      targetFolder: '',
      movingRecipeId: id,
      movingRecipeTitle: title
    });
  },

  selectTargetFolder(e) {
    const folder = e.currentTarget.dataset.folder;
    if (folder === this.data.currentFolder) {
      wx.showToast({ title: '已在该文件夹中', icon: 'none' });
      return;
    }
    this.setData({ targetFolder: folder });
  },

  hideMoveModal() {
    this.setData({
      showMoveModal: false,
      targetFolder: '',
      movingRecipeId: null,
      movingRecipeTitle: ''
    });
  },

  async confirmMove() {
    const { movingRecipeId, targetFolder } = this.data;
    
    if (!movingRecipeId || !targetFolder) {
      wx.showToast({ title: '请选择目标文件夹', icon: 'none' });
      return;
    }

    try {
      wx.showLoading({ title: '移动中...' });
      
      await request({
        url: `/favorite/foldermove?recipeId=${movingRecipeId}&folderName=${encodeURIComponent(targetFolder)}`,
        method: 'POST'
      });
      
      wx.hideLoading();
      wx.showToast({ title: `已移动到"${targetFolder}"`, icon: 'success' });
      
      const newRecipes = this.data.recipes.filter(r => r.id !== movingRecipeId);
      this.setData({ recipes: newRecipes });
      this.hideMoveModal();
      
    } catch (e) {
      wx.hideLoading();
      wx.showToast({ title: '移动失败', icon: 'none' });
    }
  },

  // ================== 删除功能 ==================
  
  async removeFavorite(e) {
    const { id } = e.currentTarget.dataset;
    
    try {
      await request({
        url: '/favorite/toggle',
        method: 'POST',
        data: {
          userId: wx.getStorageSync('userId'),
          recipeId: id,
          folderName: this.data.currentFolder
        }
      });
      
      this.setData({
        recipes: this.data.recipes.filter(r => r.id !== id)
      });
      wx.showToast({ title: '已取消收藏', icon: 'success' });
    } catch (e) {
      wx.showToast({ title: '操作失败', icon: 'none' });
    }
  },

  // ================== 新建文件夹 ==================
  
  showCreateFolder() {
    this.setData({ showCreateModal: true, newFolderName: '' });
  },

  hideCreateModal() {
    this.setData({ showCreateModal: false });
  },

  inputFolderName(e) {
    this.setData({ newFolderName: e.detail.value });
  },

  async createFolder() {
    if (!this.data.newFolderName.trim()) {
      wx.showToast({ title: '请输入文件夹名称', icon: 'none' });
      return;
    }
    
    if (this.data.folders.includes(this.data.newFolderName)) {
      wx.showToast({ title: '该文件夹已存在', icon: 'none' });
      return;
    }
    
    this.setData({
      folders: [...this.data.folders, this.data.newFolderName],
      showCreateModal: false
    });
    wx.showToast({ title: '创建成功', icon: 'success' });
  },

  refresh() {
    this.setData({ recipes: [], page: 1, hasMore: true });
    this.loadFavorites();
  },

  onReachBottom() {
    this.loadFavorites();
  }
});