<template>
  <div class="ad-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>广告管理</span>
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            添加广告
          </el-button>
        </div>
      </template>

      <!-- 广告列表 -->
      <el-table :data="advertisements" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label="类型" width="140">
          <template #default="scope">
            <el-tag :type="getTypeColor(scope.row.type)">
              {{ getTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="weight" label="权重" width="80" />
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.isActive"
              @change="toggleStatus(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="clickCount" label="点击数" width="80" />
        <el-table-column prop="viewCount" label="展示数" width="80" />
        <el-table-column label="有效期" width="200">
          <template #default="scope">
            <div v-if="scope.row.startTime || scope.row.endTime">
              {{ formatDateRange(scope.row.startTime, scope.row.endTime) }}
            </div>
            <span v-else class="text-muted">永久有效</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editAd(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="deleteAd(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑广告对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="900px"
      :before-close="handleClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="广告标题" prop="title">
              <el-input v-model="formData.title" placeholder="请输入广告标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="广告类型" prop="type">
              <el-select v-model="formData.type" placeholder="请选择广告类型" style="width: 100%">
                <el-option label="横幅广告" value="BANNER" />
                <el-option label="百度联盟" value="BAIDU" />
                <el-option label="谷歌AdSense" value="GOOGLE" />
                <el-option label="腾讯优量汇" value="TENCENT" />
                <el-option label="字节跳动穿山甲" value="BYTEDANCE" />
                <el-option label="阿里妈妈" value="ALIMAMA" />
                <el-option label="360广告" value="QIHOO" />
                <el-option label="搜狗广告" value="SOGOU" />
                <el-option label="京东联盟" value="JD" />
                <el-option label="拼多多推广" value="PDD" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="权重" prop="weight">
              <el-input-number v-model="formData.weight" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-switch v-model="formData.isActive" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 横幅广告相关字段 -->
        <template v-if="formData.type === 'BANNER'">
          <el-form-item label="图片链接" prop="imageUrl">
            <el-input v-model="formData.imageUrl" placeholder="请输入图片URL" />
          </el-form-item>
          <el-form-item label="跳转链接" prop="linkUrl">
            <el-input v-model="formData.linkUrl" placeholder="请输入跳转URL" />
          </el-form-item>
        </template>

        <!-- 百度联盟相关字段 -->
        <template v-if="formData.type === 'BAIDU'">
          <el-form-item label="百度联盟ID" prop="baiduCproId">
            <el-input v-model="formData.baiduCproId" placeholder="请输入百度联盟代码位ID" />
          </el-form-item>
        </template>

        <!-- 腾讯优量汇相关字段 -->
        <template v-if="formData.type === 'TENCENT'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="腾讯AppID" prop="tencentAppId">
                <el-input v-model="formData.tencentAppId" placeholder="请输入腾讯优量汇AppID" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="广告位ID" prop="tencentPlacementId">
                <el-input v-model="formData.tencentPlacementId" placeholder="请输入广告位ID" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 字节跳动穿山甲相关字段 -->
        <template v-if="formData.type === 'BYTEDANCE'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="字节AppID" prop="bytedanceAppId">
                <el-input v-model="formData.bytedanceAppId" placeholder="请输入穿山甲AppID" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="广告位ID" prop="bytedanceSlotId">
                <el-input v-model="formData.bytedanceSlotId" placeholder="请输入广告位SlotID" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 阿里妈妈相关字段 -->
        <template v-if="formData.type === 'ALIMAMA'">
          <el-form-item label="阿里妈妈PID" prop="alimamaPid">
            <el-input v-model="formData.alimamaPid" placeholder="请输入阿里妈妈推广位PID" />
          </el-form-item>
        </template>

        <!-- 360广告相关字段 -->
        <template v-if="formData.type === 'QIHOO'">
          <el-form-item label="360广告位ID" prop="qihooPosId">
            <el-input v-model="formData.qihooPosId" placeholder="请输入360广告位ID" />
          </el-form-item>
        </template>

        <!-- 搜狗广告相关字段 -->
        <template v-if="formData.type === 'SOGOU'">
          <el-form-item label="搜狗AppID" prop="sogouAppId">
            <el-input v-model="formData.sogouAppId" placeholder="请输入搜狗广告AppID" />
          </el-form-item>
        </template>

        <!-- 京东联盟相关字段 -->
        <template v-if="formData.type === 'JD'">
          <el-form-item label="京东联盟ID" prop="jdUnionId">
            <el-input v-model="formData.jdUnionId" placeholder="请输入京东联盟推广ID" />
          </el-form-item>
        </template>

        <!-- 拼多多推广相关字段 -->
        <template v-if="formData.type === 'PDD'">
          <el-form-item label="拼多多AppKey" prop="pddAppKey">
            <el-input v-model="formData.pddAppKey" placeholder="请输入拼多多推广AppKey" />
          </el-form-item>
        </template>

        <!-- 广告尺寸设置（除横幅广告外都需要） -->
        <template v-if="formData.type !== 'BANNER'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="宽度" prop="width">
                <el-input-number v-model="formData.width" :min="100" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="高度" prop="height">
                <el-input-number v-model="formData.height" :min="50" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 有效期设置 -->
        <el-form-item label="有效期">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入广告描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import axios from 'axios'

// 响应式数据
const loading = ref(false)
const advertisements = ref([])
const dialogVisible = ref(false)
const formRef = ref()
const editingId = ref(null)
const dateRange = ref(null)

// 表单数据
const formData = reactive({
  title: '',
  type: 'BANNER',
  imageUrl: '',
  linkUrl: '',
  baiduCproId: '',
  tencentAppId: '',
  tencentPlacementId: '',
  bytedanceAppId: '',
  bytedanceSlotId: '',
  alimamaPid: '',
  qihooPosId: '',
  sogouAppId: '',
  jdUnionId: '',
  pddAppKey: '',
  width: 300,
  height: 100,
  weight: 10,
  isActive: true,
  description: ''
})

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入广告标题', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择广告类型', trigger: 'change' }
  ],
  weight: [
    { required: true, message: '请输入权重', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => {
  return editingId.value ? '编辑广告' : '添加广告'
})

// 方法
const loadAdvertisements = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/ad/list')
    if (response.data.code === 200) {
      advertisements.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '获取广告列表失败')
    }
  } catch (error) {
    console.error('获取广告列表失败:', error)
    ElMessage.error('获取广告列表失败')
  } finally {
    loading.value = false
  }
}

const getTypeColor = (type) => {
  const colors = {
    'BANNER': 'primary',
    'BAIDU': 'success',
    'GOOGLE': 'warning',
    'TENCENT': 'info',
    'BYTEDANCE': 'danger',
    'ALIMAMA': 'success',
    'QIHOO': 'primary',
    'SOGOU': 'warning',
    'JD': 'danger',
    'PDD': 'info'
  }
  return colors[type] || 'info'
}

const getTypeText = (type) => {
  const texts = {
    'BANNER': '横幅广告',
    'BAIDU': '百度联盟',
    'GOOGLE': '谷歌AdSense',
    'TENCENT': '腾讯优量汇',
    'BYTEDANCE': '字节跳动穿山甲',
    'ALIMAMA': '阿里妈妈',
    'QIHOO': '360广告',
    'SOGOU': '搜狗广告',
    'JD': '京东联盟',
    'PDD': '拼多多推广'
  }
  return texts[type] || type
}

const formatDateRange = (startTime, endTime) => {
  const start = startTime ? new Date(startTime).toLocaleString() : '不限'
  const end = endTime ? new Date(endTime).toLocaleString() : '不限'
  return `${start} ~ ${end}`
}

const showCreateDialog = () => {
  resetForm()
  editingId.value = null
  dialogVisible.value = true
}

const editAd = (ad) => {
  resetForm()
  editingId.value = ad.id
  
  // 填充表单数据
  Object.keys(formData).forEach(key => {
    if (ad[key] !== undefined) {
      formData[key] = ad[key]
    }
  })
  
  // 设置日期范围
  if (ad.startTime || ad.endTime) {
    dateRange.value = [ad.startTime, ad.endTime]
  }
  
  dialogVisible.value = true
}

const resetForm = () => {
  Object.assign(formData, {
    title: '',
    type: 'BANNER',
    imageUrl: '',
    linkUrl: '',
    baiduCproId: '',
    tencentAppId: '',
    tencentPlacementId: '',
    bytedanceAppId: '',
    bytedanceSlotId: '',
    alimamaPid: '',
    qihooPosId: '',
    sogouAppId: '',
    jdUnionId: '',
    pddAppKey: '',
    width: 300,
    height: 100,
    weight: 10,
    isActive: true,
    description: ''
  })
  dateRange.value = null
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleClose = () => {
  resetForm()
  dialogVisible.value = false
}

const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const submitData = { ...formData }
    
    // 处理日期范围
    if (dateRange.value && dateRange.value.length === 2) {
      submitData.startTime = dateRange.value[0]
      submitData.endTime = dateRange.value[1]
    }
    
    const url = editingId.value ? `/api/ad/update/${editingId.value}` : '/api/ad/create'
    const method = editingId.value ? 'put' : 'post'
    
    const response = await axios[method](url, submitData)
    
    if (response.data.code === 200) {
      ElMessage.success(editingId.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadAdvertisements()
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  }
}

const toggleStatus = async (ad) => {
  try {
    const response = await axios.post(`/api/ad/toggle/${ad.id}`)
    if (response.data.code === 200) {
      ElMessage.success('状态切换成功')
      loadAdvertisements()
    } else {
      ElMessage.error(response.data.message || '操作失败')
      // 回滚状态
      ad.isActive = !ad.isActive
    }
  } catch (error) {
    console.error('切换状态失败:', error)
    ElMessage.error('操作失败')
    // 回滚状态
    ad.isActive = !ad.isActive
  }
}

const deleteAd = async (ad) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除广告 "${ad.title}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await axios.delete(`/api/ad/delete/${ad.id}`)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      loadAdvertisements()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 生命周期
onMounted(() => {
  loadAdvertisements()
})
</script>

<style scoped>
.ad-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-muted {
  color: #999;
}

.dialog-footer {
  text-align: right;
}
</style> 