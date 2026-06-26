Component({
  properties: {
    name: String,
    color: {
      type: String,
      value: '#ff6b6b'
    },
    icon: String,
    border: {
      type: Boolean,
      value: false
    }
  },

  data: {
    bgColor: '',
    textColor: ''
  },

  lifetimes: {
    attached() {
      const { color, border } = this.properties;
      this.setData({
        bgColor: border ? 'transparent' : (color + '20'), // 20 是 16 进制透明度
        textColor: color
      });
    }
  }
});