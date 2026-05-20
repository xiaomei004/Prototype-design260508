<script setup>
defineProps({
  visible: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(["close"]);

function onBackdropClick(event) {
  if (event.target === event.currentTarget) {
    emit("close");
  }
}
</script>

<template>
  <div v-if="visible" class="modal" role="dialog" aria-modal="true" aria-labelledby="modal-title" @click="onBackdropClick">
    <div class="modal-card">
      <button class="close-modal" type="button" aria-label="关闭" @click="$emit('close')"></button>
      <slot />
    </div>
  </div>
</template>
