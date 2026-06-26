const { request } = require('../../utils/request');

Page({
  data: {
    // 登录/注册模式切换
    isRegisterMode: false,
    
    // 登录字段
    username: '',
    password: '',
    
    // 注册额外字段
    nickname: '',
    phone: '',
    
    // 按钮状态
    loading: false
  },
  
  // ========== 输入处理 ==========
  
  inputUsername(e) {
    this.setData({ username: e.detail.value });
  },
  
  inputPassword(e) {
    this.setData({ password: e.detail.value });
  },
  
  inputNickname(e) {
    this.setData({ nickname: e.detail.value });
  },
  
  inputPhone(e) {
    this.setData({ phone: e.detail.value });
  },
  
  // ========== 模式切换 ==========
  
  switchMode() {
    this.setData({
      isRegisterMode: !this.data.isRegisterMode,
      // 清空输入（可选）
      username: '',
      password: '',
      nickname: '',
      phone: ''
    });
  },
  
  goBack() {
    wx.navigateBack();
  },
  
  // ========== 登录方法 ==========
  
  async login() {
    const { username, password } = this.data;
    
    if (!username || !password) {
      wx.showToast({ title: '请输入用户名和密码', icon: 'none' });
      return;
    }
    
    this.setData({ loading: true });
    
    try {
      const res = await request({
        url: '/user/login',
        method: 'POST',
        data: { username, password }
      });
      
      // 保存用户信息
      wx.setStorageSync('userId', res.id);
      wx.setStorageSync('userInfo', res);
      
      wx.showToast({ title: '登录成功', icon: 'success' });
      
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' });
      }, 1500);
      
    } catch (e) {
      console.error('登录失败', e);
      wx.showToast({ title: e.msg || '登录失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  },
  
  // ========== 注册方法（新增） ==========
  
  async register() {
    const { username, password, nickname, phone } = this.data;
    
    // 表单验证
    if (!username || !password) {
      wx.showToast({ title: '请输入用户名和密码', icon: 'none' });
      return;
    }
    
    if (username.length < 2) {
      wx.showToast({ title: '用户名至少2位', icon: 'none' });
      return;
    }
    
    if (password.length < 6) {
      wx.showToast({ title: '密码至少6位', icon: 'none' });
      return;
    }
    
    if (!nickname) {
      wx.showToast({ title: '请输入昵称', icon: 'none' });
      return;
    }
    
    // 手机号验证（可选）
    if (phone && !/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({ title: '手机号格式错误', icon: 'none' });
      return;
    }
    
    this.setData({ loading: true });
    
    try {
      const res = await request({
        url: '/user/register',
        method: 'POST',
        data: {
          username: username.trim(),
          password: password,
          nickname: nickname.trim(),
          phone: phone.trim()
        }
      });
      
      wx.showToast({ title: '注册成功', icon: 'success' });
      
      // 自动登录
      wx.setStorageSync('userId', res.id);
      wx.setStorageSync('userInfo', res);
      
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' });
      }, 1500);
      
    } catch (e) {
      console.error('注册失败', e);
      wx.showToast({ title: e.msg || '注册失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  },
  
  // 表单提交（根据模式判断）
  submitForm() {
    if (this.data.isRegisterMode) {
      this.register();
    } else {
      this.login();
    }
  },
  
  // 游客模式
  goGuest() {
    wx.switchTab({ url: '/pages/index/index' });
  }
});