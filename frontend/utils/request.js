const BASE_URL = 'http://localhost:8080/api';
// const BASE_URL = 'https://your-domain.com/api';

const request = (options) => {
  return new Promise((resolve, reject) => {
    const userId = wx.getStorageSync('userId');
    
    // 统一添加 userId（如果存在且未在参数中指定）
    if (userId && !options.url.includes('userId=') && !options.data?.userId) {
      const separator = options.url.includes('?') ? '&' : '?';
      options.url += `${separator}userId=${userId}`;
    }

    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 200) {
          // 获取实际数据
          let result = res.data.data;
          
          // 如果是分页嵌套结构 {data: [...], total: 10, page: 1, size: 10}
          if (result && typeof result === 'object' && 
              (result.total !== undefined || result.pages !== undefined) && 
              Array.isArray(result.data)) {
            // 返回包含分页信息和列表的对象
            resolve({
              list: result.data,
              total: result.total || 0,
              page: result.page || 1,
              size: result.size || 10,
              pages: result.pages || 1
            });
            return;
          }
          
          // 如果是普通数组或对象，直接返回
          resolve(result);
        } else {
          wx.showToast({ 
            title: res.data.msg || '请求失败', 
            icon: 'none',
            duration: 2000
          });
          reject(res.data);
        }
      },
      fail: (err) => {
        wx.showToast({ title: '网络错误', icon: 'none' });
        reject(err);
      }
    });
  });
};

module.exports = { request, BASE_URL };