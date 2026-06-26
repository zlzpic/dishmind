const { request } = require('../../../utils/request');

Page({
  data: {
    isEdit: false,
    recipeId: null,
    title: '',
    description: '',
    difficulty: 2,
    cookTime: 30,
    categories: [],
    selectedTags: [],   // ← 补上了，标签点击要用
    steps: [],
    submitting: false
  },

  onLoad(options) {
    this.loadTags();
    if (options.id) {
      this.setData({ isEdit: true, recipeId: parseInt(options.id) });
      this.loadRecipeDetail(parseInt(options.id));
    } else {
      this.addStep();
    }
  },

  // ========== 步骤相关 ==========

  addStep() {
    const steps = [...this.data.steps];
    steps.push({
      stepNumber: steps.length + 1,
      description: '',
      tip: '',
      durationSeconds: '',
      imageUrl: ''
    });
    this.setData({ steps });
  },

  deleteStep(e) {
    const index = parseInt(e.currentTarget.dataset.index);
    if (isNaN(index)) return;
    
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个步骤吗？',
      success: (res) => {
        if (res.confirm) {
          let steps = this.data.steps.filter((_, i) => i !== index);
          steps = steps.map((s, i) => ({...s, stepNumber: i + 1}));
          this.setData({ steps });
        }
      }
    });
  },

  inputStepDesc(e) { this.updateStepField(e, 'description'); },
  inputStepTip(e) { this.updateStepField(e, 'tip'); },
  
  inputStepTime(e) {
    const value = parseInt(e.detail.value) || 0;
    this.updateStepField(e, 'durationSeconds', value);
  },
  
  inputStepImage(e) { this.updateStepField(e, 'imageUrl'); },

  updateStepField(e, field, value = null) {
    const index = parseInt(e.currentTarget.dataset.index);
    if (isNaN(index)) {
      console.error('更新步骤失败：无效的 index', e.currentTarget.dataset);
      return;
    }
    const realValue = value !== null ? value : e.detail.value;
    const key = `steps[${index}].${field}`;
    this.setData({ [key]: realValue });
  },

  // ========== 表单基础 ==========

  inputTitle(e) { this.setData({ title: e.detail.value }); },
  inputDesc(e) { this.setData({ description: e.detail.value }); },
  changeDifficulty(e) { this.setData({ difficulty: e.detail.value }); },
  inputCookTime(e) { this.setData({ cookTime: parseInt(e.detail.value) || 0 }); },

  // ========== 标签（已修复） ==========

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

  toggleTag(e) {
    const { catIndex, tagId } = e.currentTarget.dataset;
    const cIdx = parseInt(catIndex);
    
    // 防御：数据未加载完或参数异常
    if (isNaN(cIdx) || !this.data.categories[cIdx]) {
      console.error('toggleTag 失败：无效的分类下标', e.currentTarget.dataset);
      return;
    }

    const categories = JSON.parse(JSON.stringify(this.data.categories));
    const tIdx = categories[cIdx].tags.findIndex(t => t.id === tagId);
    
    if (tIdx === -1) return;

    const isSelected = !categories[cIdx].tags[tIdx].selected;
    categories[cIdx].tags[tIdx].selected = isSelected;

    let selectedTags = [...this.data.selectedTags];
    const tag = categories[cIdx].tags[tIdx];

    if (isSelected) {
      selectedTags.push(tag);
    } else {
      selectedTags = selectedTags.filter(t => t.id !== tag.id);
    }

    this.setData({ categories, selectedTags });
  },

  // ========== 加载详情（编辑模式） ==========

  async loadRecipeDetail(id) {
    try {
      const recipe = await request({ url: `/recipe/${id}` });
      
      this.setData({
        title: recipe.title,
        description: recipe.description || '',
        difficulty: recipe.difficulty,
        cookTime: recipe.cookTime,
        steps: recipe.steps ? recipe.steps.map((s, i) => ({
          stepNumber: i + 1,
          description: s.description || '',
          tip: s.tip || '',
          durationSeconds: s.durationSeconds || '',
          imageUrl: s.imageUrl || ''
        })) : [{ stepNumber: 1, description: '', tip: '', durationSeconds: '', imageUrl: '' }]
      });

      // 同步标签选中状态
      if (recipe.tags && this.data.categories.length > 0) {
        const selectedIds = recipe.tags.map(t => t.id);
        const categories = this.data.categories.map(cat => ({
          ...cat,
          tags: cat.tags.map(t => ({
            ...t,
            selected: selectedIds.includes(t.id)
          }))
        }));
        
        // 同步生成 selectedTags
        const selectedTags = [];
        categories.forEach(cat => {
          cat.tags.forEach(t => {
            if (t.selected) selectedTags.push(t);
          });
        });
        
        this.setData({ categories, selectedTags });
      }
    } catch (e) {
      console.error('加载菜谱详情失败', e);
    }
  },

  // ========== 提交 ==========

  async submitRecipe() {
    if (!this.data.title.trim()) {
      wx.showToast({ title: '请输入标题', icon: 'none' });
      return;
    }
    
    const validSteps = this.data.steps.filter(s => s.description.trim());
    if (validSteps.length === 0) {
      wx.showToast({ title: '至少填写一个步骤', icon: 'none' });
      return;
    }

    this.setData({ submitting: true });

    try {
      const userId = wx.getStorageSync('userId');
      
      const selectedTags = [];
      this.data.categories.forEach(cat => {
        cat.tags.forEach(t => {
          if (t.selected) selectedTags.push(t.id);
        });
      });

      const stepsData = this.data.steps.map((s, i) => ({
        stepNumber: i + 1,
        description: s.description.trim(),
        imageUrl: s.imageUrl.trim(),
        tip: s.tip.trim(),
        durationSeconds: parseInt(s.durationSeconds) || 0
      }));

      const data = {
        title: this.data.title.trim(),
        description: this.data.description.trim(),
        coverImage: 'https://placehold.co/600x400?text=' + encodeURIComponent(this.data.title),
        difficulty: this.data.difficulty,
        cookTime: this.data.cookTime,
        tagIds: selectedTags,
        steps: stepsData
      };

      if (this.data.isEdit) {
        await request({
          url: `/recipe/${this.data.recipeId}/steps?userId=${userId}`,
          method: 'POST',
          data: stepsData
        });
        wx.showToast({ title: '保存成功', icon: 'success' });
        setTimeout(() => wx.navigateBack(), 1500);
      } else {
        const res = await request({
          url: `/recipe/create?userId=${userId}`,
          method: 'POST',
          data
        });
        wx.showToast({ title: '发布成功', icon: 'success' });
        setTimeout(() => {
          wx.redirectTo({ url: `/pages/recipe/detail/detail?id=${res.id}` });
        }, 1500);
      }
    } catch (e) {
      console.error('提交失败', e);
      wx.showToast({ title: e.msg || '提交失败', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  }
});