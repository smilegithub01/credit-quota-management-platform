<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h2>欢迎使用银行信贷额度管控平台</h2>
      <p>实时监控信贷额度使用情况，高效管控风险</p>
    </div>

    <div class="dashboard-stats">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon bg-blue">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">1,245</div>
              <div class="stat-label">客户总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon bg-green">
              <el-icon><Coin /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">¥8.6亿</div>
              <div class="stat-label">总额度</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon bg-orange">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">234</div>
              <div class="stat-label">待审批</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon bg-red">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">12</div>
              <div class="stat-label">风险预警</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <el-row :gutter="20" class="dashboard-charts">
      <el-col :span="16">
        <div class="chart-container">
          <div class="chart-header">
            <h3>额度使用趋势</h3>
          </div>
          <div id="quota-trend-chart" class="chart"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-container">
          <div class="chart-header">
            <h3>风险分布</h3>
          </div>
          <div id="risk-distribution-chart" class="chart"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <div class="chart-container">
          <div class="chart-header">
            <h3>客户类型分布</h3>
          </div>
          <div id="customer-type-chart" class="chart"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-container">
          <div class="chart-header">
            <h3>近期操作日志</h3>
          </div>
          <el-table :data="recentLogs" style="width: 100%">
            <el-table-column prop="operator" label="操作人" width="120" />
            <el-table-column prop="operation" label="操作内容" />
            <el-table-column prop="time" label="时间" width="150" />
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  setup() {
    // 近期操作日志
    const recentLogs = ref([
      { operator: '张三', operation: '新增客户：某某公司', time: '2026-02-05 10:30' },
      { operator: '李四', operation: '调整额度：某某公司', time: '2026-02-05 09:45' },
      { operator: '王五', operation: '处理风险预警', time: '2026-02-05 09:15' },
      { operator: '赵六', operation: '审批通过：额度申请', time: '2026-02-05 08:30' },
      { operator: '钱七', operation: '新增集团关系', time: '2026-02-04 17:20' }
    ])

    // 图表实例
    let quotaTrendChart = null
    let riskDistributionChart = null
    let customerTypeChart = null

    // 初始化图表
    const initCharts = () => {
      // 额度使用趋势图
      quotaTrendChart = echarts.init(document.getElementById('quota-trend-chart'))
      const quotaTrendOption = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['总额度', '已用额度', '可用额度']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        series: [
          {
            name: '总额度',
            type: 'line',
            stack: '总量',
            data: [12000000, 13200000, 10100000, 13400000, 9000000, 23000000, 21000000]
          },
          {
            name: '已用额度',
            type: 'line',
            stack: '总量',
            data: [2200000, 1820000, 1910000, 2340000, 2900000, 3300000, 3100000]
          },
          {
            name: '可用额度',
            type: 'line',
            stack: '总量',
            data: [9800000, 11380000, 8190000, 11060000, 6100000, 19700000, 17900000]
          }
        ]
      }
      quotaTrendChart.setOption(quotaTrendOption)

      // 风险分布饼图
      riskDistributionChart = echarts.init(document.getElementById('risk-distribution-chart'))
      const riskDistributionOption = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '风险等级',
            type: 'pie',
            radius: '50%',
            data: [
              { value: 1048, name: '低风险', itemStyle: { color: '#52c41a' } },
              { value: 735, name: '中风险', itemStyle: { color: '#faad14' } },
              { value: 580, name: '高风险', itemStyle: { color: '#ff7a45' } },
              { value: 484, name: '危急风险', itemStyle: { color: '#f5222d' } }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
      riskDistributionChart.setOption(riskDistributionOption)

      // 客户类型分布图
      customerTypeChart = echarts.init(document.getElementById('customer-type-chart'))
      const customerTypeOption = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {},
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value'
        },
        yAxis: {
          type: 'category',
          data: ['企业', '个人', '金融机构', '政府机构']
        },
        series: [
          {
            name: '客户数量',
            type: 'bar',
            label: {
              show: true,
              position: 'right'
            },
            data: [800, 300, 100, 45]
          }
        ]
      }
      customerTypeChart.setOption(customerTypeOption)
    }

    // 响应式处理
    const resizeCharts = () => {
      quotaTrendChart?.resize()
      riskDistributionChart?.resize()
      customerTypeChart?.resize()
    }

    onMounted(() => {
      initCharts()
      window.addEventListener('resize', resizeCharts)
    })

    onUnmounted(() => {
      window.removeEventListener('resize', resizeCharts)
      quotaTrendChart?.dispose()
      riskDistributionChart?.dispose()
      customerTypeChart?.dispose()
    })

    return {
      recentLogs
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;
  
  .dashboard-header {
    margin-bottom: 20px;
    
    h2 {
      margin: 0 0 10px 0;
      font-size: 24px;
      color: #303133;
    }
    
    p {
      margin: 0;
      color: #909399;
    }
  }
  
  .dashboard-stats {
    margin-bottom: 20px;
    
    .stat-card {
      display: flex;
      align-items: center;
      padding: 20px;
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
      }
      
      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 15px;
        
        .el-icon {
          font-size: 24px;
          color: #fff;
        }
      }
      
      .stat-content {
        flex: 1;
        
        .stat-number {
          font-size: 24px;
          font-weight: bold;
          color: #303133;
          margin-bottom: 5px;
        }
        
        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
    
    .bg-blue {
      background: linear-gradient(135deg, #74b9ff, #0984e3);
    }
    
    .bg-green {
      background: linear-gradient(135deg, #00b894, #00cec9);
    }
    
    .bg-orange {
      background: linear-gradient(135deg, #fdcb6e, #e17055);
    }
    
    .bg-red {
      background: linear-gradient(135deg, #ff7675, #d63031);
    }
  }
  
  .dashboard-charts {
    margin-bottom: 20px;
  }
  
  .chart-container {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    padding: 20px;
    margin-bottom: 20px;
    
    .chart-header {
      margin-bottom: 15px;
      
      h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }
    
    .chart {
      height: 300px;
    }
  }
}
</style>