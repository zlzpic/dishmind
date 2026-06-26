const { getDifficultyText, getPlaceholderColor } = require('../../utils/util');

Component({
  properties: {
    recipe: {
      type: Object,
      value: {}
    },
    showTag: {
      type: Boolean,
      value: true
    },
    showAuthor: {
      type: Boolean,
      value: true
    }
  },

  data: {
    placeholderColor: '#ff6b6b',
    difficultyText: ''
  },

  lifetimes: {
    attached() {
      const { recipe } = this.properties;
      this.setData({
        placeholderColor: getPlaceholderColor(recipe.coverImage || recipe.title || 'recipe'),
        difficultyText: getDifficultyText(recipe.difficulty)
      });
    }
  },

  // methods: {
  //   onTap() {
  //     const { recipe } = this.properties;
  //     if (!recipe || !recipe.id) {
  //       console.error('recipe-card: 缺少recipe或id', recipe);
  //       return;
  //     }
  //     // 确保传递的是数字类型
  //     this.triggerEvent('tap', { 
  //       id: parseInt(recipe.id) 
  //     });
  //   }
  // }
  methods: {
    onTap() {
      const { recipe } = this.properties;
      
      if (!recipe || !recipe.id) {
        console.error('recipe-card: recipe 或 id 不存在', recipe);
        return;
      }
      
      const id = parseInt(recipe.id);
      if (isNaN(id)) {
        console.error('recipe-card: 无效的 id', recipe.id);
        return;
      }
      
      // 只触发事件，不处理业务逻辑
      this.triggerEvent('tap', { id: id });
    }
  }
});