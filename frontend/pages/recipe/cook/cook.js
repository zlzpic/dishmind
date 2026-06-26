const { request } = require('../../../utils/request');
const { formatDuration } = require('../../../utils/util');

Page({
  data: {
    steps: [],
    currentIndex: 0,
    currentStep: {},
    placeholderColor: '#ff6b6b',
    timerSeconds: 0,
    timerRunning: false,
    timerDisplay: '00:00',
    showComplete: false,
    touchStartX: 0
  },

  timerInterval: null,

  async onLoad(options) {
    const id = parseInt(options.id);
    await this.loadSteps(id);
  },

  onUnload() {
    this.stopTimer();
  },

  async loadSteps(id) {
    try {
      // 从详情接口获取步骤（更完整）
      const recipe = await request({ url: `/recipe/${id}` });
      const steps = recipe.steps || [];
      
      this.setData({
        steps,
        currentStep: steps[0] || {},
        placeholderColor: this.getRandomColor()
      });
      
      if (steps[0] && steps[0].durationSeconds) {
        this.initTimer(steps[0].durationSeconds);
      }
    } catch (e) {
      console.error(e);
    }
  },

  initTimer(seconds) {
    this.setData({
      timerSeconds: seconds,
      timerDisplay: formatDuration(seconds)
    });
  },

  toggleTimer() {
    if (this.data.timerRunning) {
      this.stopTimer();
    } else {
      this.startTimer();
    }
  },

  startTimer() {
    if (this.data.timerSeconds <= 0) return;
    
    this.setData({ timerRunning: true });
    this.timerInterval = setInterval(() => {
      const seconds = this.data.timerSeconds - 1;
      this.setData({
        timerSeconds: seconds,
        timerDisplay: formatDuration(seconds)
      });
      
      if (seconds <= 0) {
        this.stopTimer();
        wx.vibrateShort({ type: 'light' });
        wx.showToast({ title: '时间到！', icon: 'none' });
      }
    }, 1000);
  },

  stopTimer() {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
    this.setData({ timerRunning: false });
  },

  resetTimer() {
    this.stopTimer();
    const duration = this.data.currentStep.durationSeconds || 0;
    this.initTimer(duration);
  },

  prevStep() {
    if (this.data.currentIndex === 0) return;
    this.changeStep(this.data.currentIndex - 1);
  },

  nextStep() {
    if (this.data.currentIndex === this.data.steps.length - 1) {
      this.setData({ showComplete: true });
      return;
    }
    this.changeStep(this.data.currentIndex + 1);
  },

  changeStep(index) {
    this.stopTimer();
    const step = this.data.steps[index];
    
    this.setData({
      currentIndex: index,
      currentStep: step,
      showComplete: false
    });
    
    if (step.durationSeconds) {
      this.initTimer(step.durationSeconds);
    }
  },

  touchStart(e) {
    this.setData({ touchStartX: e.touches[0].pageX });
  },

  touchEnd(e) {
    const diff = e.changedTouches[0].pageX - this.data.touchStartX;
    if (Math.abs(diff) > 50) {
      if (diff > 0) {
        this.prevStep();
      } else {
        this.nextStep();
      }
    }
  },

  goBack() {
    wx.navigateBack();
  },

  getRandomColor() {
    const colors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57'];
    return colors[Math.floor(Math.random() * colors.length)];
  }
});