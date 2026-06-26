const { formatDuration, getPlaceholderColor } = require('../../utils/util');

Component({
  properties: {
    step: {
      type: Object,
      value: {}
    },
    showTimer: {
      type: Boolean,
      value: true
    }
  },

  data: {
    displayOrder: 1,
    durationText: '',
    placeholderColor: '#ff6b6b',
    imageError: false
  },

  lifetimes: {
    attached() {
      const { step } = this.properties;
      this.setData({
        displayOrder: step.displayOrder || step.stepNumber || 1,
        durationText: formatDuration(step.durationSeconds || 0),
        placeholderColor: getPlaceholderColor(step.description || 'step'),
        imageError: false  // 重置错误状态
      });
    }
  },

  methods: {
    // 预览图片
    previewImage() {
      const { step } = this.properties;
      // 出错或无图时不预览
      if (this.data.imageError || !step.imageUrl) return;
      
      wx.previewImage({
        urls: [step.imageUrl]
      });
    },

    // 点击计时器
    onTimerTap() {
      const { step } = this.properties;
      if (step.durationSeconds > 0) {
        this.triggerEvent('timer', {
          seconds: step.durationSeconds,
          description: step.description
        });
      }
    },

    // 图片加载失败
    onImageError() {
      console.log('步骤图片加载失败，显示占位图');
      this.setData({ imageError: true });
    }
  }
});