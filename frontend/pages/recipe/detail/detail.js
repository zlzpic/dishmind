const { request } = require('../../../utils/request');
const { startViewTimer, endViewTimer, reportBehavior } = require('../../../utils/behavior');
const { formatDuration, getPlaceholderColor, getAvatarUrl, getDifficultyText } = require('../../../utils/util'); 
Page({
  data: {
    recipe: {},
    steps: [],
    similar: [],
    placeholderColor: '#ff6b6b',
    isAuthor: false,
    recipeId: null,
    recipe: {},
    steps: [],
    similar: [],
    placeholderColor: '#ff6b6b',
    difficultyText:null,
    isAuthor: false,
    recipeId: null,
    
    // 收藏夹弹窗相关（新增）
    authorAvatar: '/images/avatar/default.png',  // 默认头像
    showFolderModal: false,
    folders: [],
    selectedFolder: '默认收藏夹',
    newFolderName: '',
    isFavorited: false  // 确保有这个字段跟踪收藏状态
  },

  onLoad(options) {
    // 强制登录检查
    const userId = wx.getStorageSync('userId');
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      setTimeout(() => {
        wx.redirectTo({ url: '/pages/login/login' });
      }, 1500);
      return;
    }
    
    
    // ID 校验
    const id = parseInt(options.id);
    
    if (!id || isNaN(id) || id <= 0) {
      wx.showToast({ title: '无效的菜谱ID', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 1500);
      return;
    }
    
    this.setData({ recipeId: id });
    this.loadDetail(id);
    startViewTimer(id);
  },

  onUnload() {
    endViewTimer();
  },
  async onLoad(options) {
    const id = parseInt(options.id);
    if (!id || isNaN(id)) {
      wx.showToast({ title: '无效的菜谱ID', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 1500);
      return;
    }
    this.setData({ recipeId: id });
    await this.loadDetail(id);
    startViewTimer(id);
  },

  async loadDetail(id) {
    try {
      const userId = wx.getStorageSync('userId');
      const recipe = await request({ 
        url: `/recipe/${id}${userId ? '?userId=' + userId : ''}` 
      });
      
      // 处理步骤...
      const steps = (recipe.steps || []).map(s => ({
        ...s,
        durationText: formatDuration(s.durationSeconds)
      }));
      
      // 检查是否是作者
      const currentUserId = wx.getStorageSync('userId');
      const isAuthor = currentUserId && recipe.authorId === currentUserId;
      
      // 处理作者头像
      const authorAvatar = getAvatarUrl(recipe.authorAvatarUrl);
      
      this.setData({
        recipe,
        steps,
        placeholderColor: getPlaceholderColor(recipe.coverImage || recipe.title),
        difficultyText:getDifficultyText(recipe.difficulty),
        isAuthor,
        authorAvatar
      });
      
      // 关键：加载相似推荐（确保这行代码执行了）
      await this.loadSimilar(id);
      
      // 调试：检查数据
      console.log('详情加载完成，similar数据：', this.data.similar);
      
    } catch (e) {
      console.error('加载详情失败', e);
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },
 // 作者头像加载失败时使用默认头像
 onAuthorAvatarError() {
  this.setData({
    authorAvatar: '/images/avatar/default.png'
  });
},
async loadSimilar(id) {
  try {
    console.log('开始加载相似推荐，id:', id);  // 调试日志
    
    const res = await request({ 
      url: `/recipe/${id}/similar?limit=10` 
    });
    
    console.log('相似推荐接口返回：', res);  // 调试日志
    
    // 注意：res 可能是数组，也可能是 {list: [...]} 格式，需要兼容
    const list = Array.isArray(res) ? res : (res.list || []);
    
    const similar = list.map(item => ({
      ...item,
      placeholderColor: getPlaceholderColor(item.coverImage || item.title),
      difficultyText: getDifficultyText(item.difficulty)
    }));
    
    console.log('处理后的 similar：', similar);  // 调试日志
    
    this.setData({ similar });
    
  } catch (e) {
    console.error('加载相似推荐失败：', e);
    // 失败时设置为空数组，避免 undefined
    this.setData({ similar: [] });
  }
},

  async toggleFavorite() {
    const userId = wx.getStorageSync('userId');
    if (!userId) {
      wx.navigateTo({ url: '/pages/login/login' });
      return;
    }

    try {
      await request({
        url: '/favorite/toggle',
        method: 'POST',
        data: {
          userId,
          recipeId: this.data.recipeId,
          folderName: '默认收藏夹'
        }
      });
      
      this.setData({
        'recipe.isFavorited': !this.data.recipe.isFavorited
      });
      
      wx.showToast({
        title: this.data.recipe.isFavorited ? '已收藏' : '已取消',
        icon: 'success'
      });
      
      // 上报收藏行为
      if (this.data.recipe.isFavorited) {
        reportBehavior('COLLECT', 0, this.data.recipeId);
      }
    } catch (e) {
      console.error('收藏失败', e);
    }
  },

  async toggleDislike() {
    const userId = wx.getStorageSync('userId');
    if (!userId) return;
    
    try {
      await request({
        url: `/recipe/${this.data.recipeId}/dislike?userId=${userId}`,
        method: 'POST'
      });
      
      reportBehavior('DISLIKE', 0, this.data.recipeId);
      wx.showToast({ title: '已减少此类推荐', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 1500);
    } catch (e) {
      console.error(e);
    }
  },

  editRecipe() {
    wx.navigateTo({
      url: `/pages/recipe/edit/edit?id=${this.data.recipeId}`
    });
  },

  goCookMode() {
    wx.navigateTo({
      url: `/pages/recipe/cook/cook?id=${this.data.recipeId}`
    });
  },

  previewImage(e) {
    const url = e.currentTarget.dataset.url;
    wx.previewImage({ urls: [url] });
  },

  goTag(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/category/category?tagId=${id}` });
  },
  async toggleFavorite() {
    const userId = wx.getStorageSync('userId');
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      setTimeout(() => wx.navigateTo({ url: '/pages/login/login' }), 1500);
      return;
    }

    // 如果已收藏，直接取消
    if (this.data.recipe.isFavorited) {
      try {
        await request({
          url: '/favorite/toggle',
          method: 'POST',
          data: {
            userId,
            recipeId: this.data.recipeId,
            folderName: this.data.selectedFolder // 取消时也传，后端会处理
          }
        });
        
        this.setData({
          'recipe.isFavorited': false
        });
        
        wx.showToast({ title: '已取消收藏', icon: 'success' });
      } catch (e) {
        wx.showToast({ title: '操作失败', icon: 'none' });
      }
      return;
    }

    // 未收藏，显示选择文件夹弹窗
    await this.loadFolders();
    this.setData({
      showFolderModal: true,
      selectedFolder: '默认收藏夹', // 默认选中
      newFolderName: ''
    });
  },

  // 加载文件夹列表
  async loadFolders() {
    try {
      const res = await request({ url: '/favorite/folders' });
      const folders = res || ['默认收藏夹'];
      this.setData({ folders });
      
      // 如果默认收藏夹不在列表中，添加它
      if (!folders.includes('默认收藏夹')) {
        this.setData({ folders: ['默认收藏夹', ...folders] });
      }
    } catch (e) {
      console.error('加载文件夹失败', e);
      // 使用默认值
      this.setData({ folders: ['默认收藏夹'] });
    }
  },

  // 选择文件夹
  selectFolder(e) {
    const folder = e.currentTarget.dataset.folder;
    this.setData({ 
      selectedFolder: folder,
      newFolderName: '' // 选择已有文件夹时清空新建输入
    });
  },

  // 输入新文件夹名
  inputNewFolder(e) {
    this.setData({ 
      newFolderName: e.detail.value,
      // 如果输入了新建文件夹名，取消选择已有文件夹
      selectedFolder: e.detail.value ? '' : (this.data.folders[0] || '默认收藏夹')
    });
  },

  // 清空新建输入
  clearNewFolder() {
    this.setData({ 
      newFolderName: '',
      selectedFolder: this.data.folders[0] || '默认收藏夹'
    });
  },

  // 关闭弹窗
  closeFolderModal() {
    this.setData({
      showFolderModal: false,
      newFolderName: ''
    });
  },

  // 确认收藏
  async confirmFavorite() {
    const { newFolderName, selectedFolder, recipeId } = this.data;
    
    // 确定最终使用的文件夹名
    const folderName = newFolderName.trim() || selectedFolder;
    
    if (!folderName) {
      wx.showToast({ title: '请选择或输入文件夹', icon: 'none' });
      return;
    }

    try {
      wx.showLoading({ title: '收藏中...' });
      
      await request({
        url: '/favorite/toggle',
        method: 'POST',
        data: {
          userId: wx.getStorageSync('userId'),
          recipeId: recipeId,
          folderName: folderName
        }
      });
      
      wx.hideLoading();
      wx.showToast({ title: '收藏成功', icon: 'success' });
      
      // 更新收藏状态
      this.setData({
        'recipe.isFavorited': true,
        showFolderModal: false,
        newFolderName: ''
      });
      
      // 上报收藏行为
      reportBehavior('COLLECT', 0, recipeId);
      
    } catch (e) {
      wx.hideLoading();
      wx.showToast({ title: e.msg || '收藏失败', icon: 'none' });
    }
  },

  goSimilar(e) {
    const id = e.currentTarget.dataset.id;
    wx.redirectTo({ url: `/pages/recipe/detail/detail?id=${id}` });
  }
});