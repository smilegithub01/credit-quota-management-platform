<template>
  <div class="system-config-container">
    <div class="card">
      <div class="detail-title">{{ $t('message.system.systemConfig') }}</div>
      
      <el-tabs v-model="activeTab" class="config-tabs">
        <el-tab-pane :label="$t('message.system.config')" name="basic">
          <el-form 
            :model="configForm" 
            :rules="configRules"
            ref="configFormRef"
            label-width="150px"
            class="config-form"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="API基础URL" prop="apiBaseUrl">
                  <el-input v-model="configForm.apiBaseUrl" placeholder="请输入API基础URL" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="超时时间(ms)" prop="timeout">
                  <el-input-number v-model="configForm.timeout" :min="1000" :max="30000" :step="1000" />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="页面主题" prop="theme">
                  <el-select v-model="configForm.theme" placeholder="请选择页面主题">
                    <el-option label="默认主题" value="default" />
                    <el-option label="深色主题" value="dark" />
                    <el-option label="蓝色主题" value="blue" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="语言设置" prop="language">
                  <el-select v-model="configForm.language" placeholder="请选择语言">
                    <el-option label="简体中文" value="zh-CN" />
                    <el-option label="English" value="en-US" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="分页大小" prop="pageSize">
                  <el-select v-model="configForm.pageSize" placeholder="请选择分页大小">
                    <el-option label="10" :value="10" />
                    <el-option label="20" :value="20" />
                    <el-option label="50" :value="50" />
                    <el-option label="100" :value="100" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="通知显示时长">
                  <el-slider 
                    v-model="configForm.notificationDuration" 
                    :min="3" 
                    :max="10" 
                    :step="1"
                    show-input
                    show-stops
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="启用调试模式">
              <el-switch v-model="configForm.debugMode" />
              <div class="form-help">开启后将在控制台输出更多调试信息</div>
            </el-form-item>
            
            <el-form-item label="启用数据缓存">
              <el-switch v-model="configForm.enableCache" />
              <div class="form-help">开启后将缓存部分数据以提高页面加载速度</div>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSaveBasicConfig">{{ $t('message.common.save') }}</el-button>
              <el-button @click="handleResetBasicConfig">{{ $t('message.common.reset') }}</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane :label="$t('message.quota.title')" name="quota">
          <el-form 
            :model="quotaConfigForm" 
            label-width="180px"
            class="config-form"
          >
            <el-alert
              title="额度管理相关配置"
              type="info"
              :closable="false"
              style="margin-bottom: 20px;"
            />
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="额度最小金额" prop="minQuotaAmount">
                  <el-input-number 
                    v-model="quotaConfigForm.minQuotaAmount" 
                    :precision="2" 
                    :step="1000" 
                    :min="0"
                    :max="10000000"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="额度最大金额" prop="maxQuotaAmount">
                  <el-input-number 
                    v-model="quotaConfigForm.maxQuotaAmount" 
                    :precision="2" 
                    :step="10000" 
                    :min="0"
                    :max="1000000000"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="额度有效期(天)" prop="quotaValidityDays">
                  <el-input-number v-model="quotaConfigForm.quotaValidityDays" :min="1" :max="3650" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="预警阈值(%)" prop="warningThreshold">
                  <el-slider 
                    v-model="quotaConfigForm.warningThreshold" 
                    :min="0" 
                    :max="100" 
                    :step="1"
                    show-input
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="启用集团额度联动">
                  <el-switch v-model="quotaConfigForm.enableGroupQuotaLinkage" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="启用额度预占功能">
                  <el-switch v-model="quotaConfigForm.enablePreOccupancy" />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item>
              <el-button type="primary" @click="handleSaveQuotaConfig">保存额度配置</el-button>
              <el-button @click="handleResetQuotaConfig">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane :label="$t('message.risk.title')" name="risk">
          <el-form 
            :model="riskConfigForm" 
            label-width="180px"
            class="config-form"
          >
            <el-alert
              title="风险管理相关配置"
              type="info"
              :closable="false"
              style="margin-bottom: 20px;"
            />
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="风险指标检查频率(分钟)" prop="riskCheckInterval">
                  <el-input-number v-model="riskConfigForm.riskCheckInterval" :min="1" :max="1440" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="风险预警通知方式">
                  <el-checkbox-group v-model="riskConfigForm.notificationMethods">
                    <el-checkbox label="email">邮件</el-checkbox>
                    <el-checkbox label="sms">短信</el-checkbox>
                    <el-checkbox label="system">系统通知</el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="低风险阈值" prop="lowRiskThreshold">
                  <el-slider 
                    v-model="riskConfigForm.lowRiskThreshold" 
                    :min="0" 
                    :max="100" 
                    :step="1"
                    show-input
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="中风险阈值" prop="mediumRiskThreshold">
                  <el-slider 
                    v-model="riskConfigForm.mediumRiskThreshold" 
                    :min="0" 
                    :max="100" 
                    :step="1"
                    show-input
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="高风险阈值" prop="highRiskThreshold">
                  <el-slider 
                    v-model="riskConfigForm.highRiskThreshold" 
                    :min="0" 
                    :max="100" 
                    :step="1"
                    show-input
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="危急风险阈值" prop="criticalRiskThreshold">
                  <el-slider 
                    v-model="riskConfigForm.criticalRiskThreshold" 
                    :min="0" 
                    :max="100" 
                    :step="1"
                    show-input
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="启用自动风险评估">
              <el-switch v-model="riskConfigForm.enableAutoAssessment" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSaveRiskConfig">保存风险配置</el-button>
              <el-button @click="handleResetRiskConfig">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane :label="$t('message.approval.title')" name="approval">
          <el-form 
            :model="approvalConfigForm" 
            label-width="180px"
            class="config-form"
          >
            <el-alert
              title="审批流程相关配置"
              type="info"
              :closable="false"
              style="margin-bottom: 20px;"
            />
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="审批超时提醒(小时)" prop="approvalTimeoutHours">
                  <el-input-number v-model="approvalConfigForm.approvalTimeoutHours" :min="1" :max="720" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="自动转办超时(小时)" prop="autoTransferHours">
                  <el-input-number v-model="approvalConfigForm.autoTransferHours" :min="1" :max="720" />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="审批节点最大数量" prop="maxApprovalNodes">
                  <el-input-number v-model="approvalConfigForm.maxApprovalNodes" :min="1" :max="20" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="审批优先级">
                  <el-radio-group v-model="approvalConfigForm.prioritySetting">
                    <el-radio label="normal">普通</el-radio>
                    <el-radio label="high">高</el-radio>
                    <el-radio label="urgent">紧急</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="启用审批节点超时自动处理">
              <el-switch v-model="approvalConfigForm.enableAutoTimeoutHandling" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSaveApprovalConfig">保存审批配置</el-button>
              <el-button @click="handleResetApprovalConfig">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'SystemConfig',
  setup() {
    // 激活的标签页
    const activeTab = ref('basic')
    
    // 基础配置表单
    const configForm = reactive({
      apiBaseUrl: 'http://localhost:8080/api/unified-credit',
      timeout: 15000,
      theme: 'default',
      language: 'zh-CN',
      pageSize: 10,
      notificationDuration: 5,
      debugMode: false,
      enableCache: true
    })
    
    // 基础配置验证规则
    const configRules = {
      apiBaseUrl: [
        { required: true, message: '请输入API基础URL', trigger: 'blur' },
        { pattern: /^https?:\/\/.+/, message: '请输入有效的URL', trigger: 'blur' }
      ],
      timeout: [
        { required: true, message: '请输入超时时间', trigger: 'blur' },
        { type: 'number', min: 1000, max: 30000, message: '超时时间应在1000-30000ms之间', trigger: 'blur' }
      ]
    }
    
    // 额度配置表单
    const quotaConfigForm = reactive({
      minQuotaAmount: 1000.00,
      maxQuotaAmount: 10000000.00,
      quotaValidityDays: 365,
      warningThreshold: 80,
      enableGroupQuotaLinkage: true,
      enablePreOccupancy: true
    })
    
    // 风险配置表单
    const riskConfigForm = reactive({
      riskCheckInterval: 60,
      notificationMethods: ['email', 'system'],
      lowRiskThreshold: 30,
      mediumRiskThreshold: 60,
      highRiskThreshold: 85,
      criticalRiskThreshold: 95,
      enableAutoAssessment: true
    })
    
    // 审批配置表单
    const approvalConfigForm = reactive({
      approvalTimeoutHours: 24,
      autoTransferHours: 48,
      maxApprovalNodes: 5,
      prioritySetting: 'normal',
      enableAutoTimeoutHandling: false
    })
    
    // 表单引用
    const configFormRef = ref(null)
    
    // 保存基础配置
    const handleSaveBasicConfig = async () => {
      try {
        if (configFormRef.value) {
          await configFormRef.value.validate()
        }
        
        // TODO: 实际保存配置的API调用
        ElMessage.success('基础配置保存成功')
        
        // 更新系统配置
        const config = {
          apiUrl: configForm.apiBaseUrl,
          theme: configForm.theme,
          language: configForm.language
        }
        // this.$store.dispatch('updateSystemConfig', config)
      } catch (error) {
        ElMessage.error('配置验证失败: ' + error.message)
      }
    }
    
    // 重置基础配置
    const handleResetBasicConfig = () => {
      // 恢复默认值
      Object.assign(configForm, {
        apiBaseUrl: 'http://localhost:8080/api/unified-credit',
        timeout: 15000,
        theme: 'default',
        language: 'zh-CN',
        pageSize: 10,
        notificationDuration: 5,
        debugMode: false,
        enableCache: true
      })
      ElMessage.info('已重置为默认配置')
    }
    
    // 保存额度配置
    const handleSaveQuotaConfig = () => {
      // TODO: 实际保存配置的API调用
      ElMessage.success('额度配置保存成功')
    }
    
    // 重置额度配置
    const handleResetQuotaConfig = () => {
      Object.assign(quotaConfigForm, {
        minQuotaAmount: 1000.00,
        maxQuotaAmount: 10000000.00,
        quotaValidityDays: 365,
        warningThreshold: 80,
        enableGroupQuotaLinkage: true,
        enablePreOccupancy: true
      })
      ElMessage.info('已重置额度配置')
    }
    
    // 保存风险配置
    const handleSaveRiskConfig = () => {
      // TODO: 实际保存配置的API调用
      ElMessage.success('风险配置保存成功')
    }
    
    // 重置风险配置
    const handleResetRiskConfig = () => {
      Object.assign(riskConfigForm, {
        riskCheckInterval: 60,
        notificationMethods: ['email', 'system'],
        lowRiskThreshold: 30,
        mediumRiskThreshold: 60,
        highRiskThreshold: 85,
        criticalRiskThreshold: 95,
        enableAutoAssessment: true
      })
      ElMessage.info('已重置风险配置')
    }
    
    // 保存审批配置
    const handleSaveApprovalConfig = () => {
      // TODO: 实际保存配置的API调用
      ElMessage.success('审批配置保存成功')
    }
    
    // 重置审批配置
    const handleResetApprovalConfig = () => {
      Object.assign(approvalConfigForm, {
        approvalTimeoutHours: 24,
        autoTransferHours: 48,
        maxApprovalNodes: 5,
        prioritySetting: 'normal',
        enableAutoTimeoutHandling: false
      })
      ElMessage.info('已重置审批配置')
    }
    
    // 初始化数据
    onMounted(() => {
      // 从系统配置中加载现有配置
      // const systemConfig = this.$store.getters.systemConfig
      // if (systemConfig) {
      //   Object.assign(configForm, {
      //     apiBaseUrl: systemConfig.apiUrl,
      //     theme: systemConfig.theme,
      //     language: systemConfig.language
      //   })
      // }
    })
    
    return {
      activeTab,
      configForm,
      configRules,
      quotaConfigForm,
      riskConfigForm,
      approvalConfigForm,
      configFormRef,
      handleSaveBasicConfig,
      handleResetBasicConfig,
      handleSaveQuotaConfig,
      handleResetQuotaConfig,
      handleSaveRiskConfig,
      handleResetRiskConfig,
      handleSaveApprovalConfig,
      handleResetApprovalConfig
    }
  }
}
</script>

<style lang="scss" scoped>
.system-config-container {
  .config-tabs {
    :deep(.el-tab-pane) {
      padding: 20px 0;
    }
  }
  
  .config-form {
    max-width: 800px;
    
    .form-help {
      color: #909399;
      font-size: 12px;
      margin-top: 5px;
    }
  }
}
</style>