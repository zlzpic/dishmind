Component({
  properties: {
    loading: {
      type: Boolean,
      value: false
    },
    noMore: {
      type: Boolean,
      value: false
    },
    empty: {
      type: Boolean,
      value: false
    },
    loadingText: {
      type: String,
      value: '加载中...'
    },
    noMoreText: {
      type: String,
      value: '没有更多了'
    },
    emptyText: {
      type: String,
      value: '暂无数据'
    },
    emptyIcon: {
      type: String,
      value: '📭'
    },
    showNoMore: {
      type: Boolean,
      value: true
    },
    showEmpty: {
      type: Boolean,
      value: true
    }
  }
});