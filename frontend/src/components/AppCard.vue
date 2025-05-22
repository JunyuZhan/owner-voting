<template>
  <div class="app-card" :class="{ 'is-hoverable': hoverable }">
    <div class="app-card-header" v-if="$slots.header || title">
      <slot name="header">
        <div class="app-card-title">{{ title }}</div>
        <div class="app-card-subtitle" v-if="subtitle">{{ subtitle }}</div>
      </slot>
      <div class="app-card-header-extra" v-if="$slots.extra">
        <slot name="extra"></slot>
      </div>
    </div>
    <div class="app-card-body" :style="bodyStyle">
      <slot></slot>
    </div>
    <div class="app-card-footer" v-if="$slots.footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: ''
  },
  subtitle: {
    type: String,
    default: ''
  },
  bodyStyle: {
    type: Object,
    default: () => ({})
  },
  hoverable: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped>
.app-card {
  background-color: white;
  border-radius: var(--app-border-radius-md);
  box-shadow: var(--app-shadow-sm);
  margin-bottom: var(--app-spacing-lg);
  transition: all 0.3s ease;
  overflow: hidden;
}

.app-card.is-hoverable:hover {
  transform: translateY(-4px);
  box-shadow: var(--app-shadow-md);
}

.app-card-header {
  padding: var(--app-spacing-md) var(--app-spacing-lg);
  border-bottom: 1px solid var(--el-color-border-lighter);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.app-card-title {
  font-size: var(--app-font-size-lg);
  font-weight: var(--app-font-weight-bold);
  color: var(--el-color-text-primary);
  line-height: 1.4;
}

.app-card-subtitle {
  font-size: var(--app-font-size-base);
  color: var(--el-color-text-secondary);
  margin-top: var(--app-spacing-xs);
}

.app-card-body {
  padding: var(--app-spacing-lg);
}

.app-card-footer {
  padding: var(--app-spacing-md) var(--app-spacing-lg);
  border-top: 1px solid var(--el-color-border-lighter);
  background-color: var(--el-color-primary-light-9);
}

@media (max-width: 768px) {
  .app-card-header,
  .app-card-body,
  .app-card-footer {
    padding: var(--app-spacing-md);
  }
}
</style> 