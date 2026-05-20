<script setup>
import { computed, onMounted, ref } from "vue";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";
import ResultModal from "../components/ResultModal.vue";
import { useAnimalStore } from "../stores/animal";

const router = useRouter();
const animalStore = useAnimalStore();
const { animals, feedItems, loadingPosts, postError, posting } = storeToRefs(animalStore);

const publishVisible = ref(false);
const publishError = ref("");
const publishForm = ref({
  type: "checkin",
  animalId: "",
  location: "",
  content: ""
});

const animalOptions = computed(() => animals.value.map((animal) => ({
  label: `${animal.name} · ${animal.location}`,
  value: animal.id
})));

onMounted(async () => {
  await Promise.allSettled([
    animalStore.fetchFeedItems(),
    animalStore.fetchAnimals()
  ]);
});

function openPublishModal() {
  publishError.value = "";
  publishVisible.value = true;
}

function closePublishModal() {
  publishVisible.value = false;
  publishError.value = "";
  publishForm.value = {
    type: "checkin",
    animalId: "",
    location: "",
    content: ""
  };
}

async function submitPost() {
  publishError.value = "";
  try {
    await animalStore.addPost({
      type: publishForm.value.type,
      animalId: publishForm.value.animalId ? Number(publishForm.value.animalId) : null,
      location: publishForm.value.location,
      content: publishForm.value.content
    });
    closePublishModal();
  } catch (error) {
    publishError.value = error?.message || "发布失败，请稍后重试。";
  }
}
</script>

<template>
  <section class="page active" id="discover-page">
    <div class="hero-panel">
      <div>
        <p>今日巡护</p>
        <strong>让每一次救助都有档可查</strong>
      </div>
      <button class="primary-action" type="button" @click="router.push({ name: 'camera' })">随手识别</button>
    </div>

    <div class="stats-grid">
      <article>
        <span>{{ animalStore.animalCount }}</span>
        <p>已建档动物</p>
      </article>
      <article>
        <span>18</span>
        <p>今日投喂</p>
      </article>
      <article>
        <span>42h</span>
        <p>志愿服务</p>
      </article>
    </div>

    <div class="alert-strip">
      <span>紧急</span>
      <p>教学楼 B 区发现疑似受伤幼猫，动保协会正在协调救助。</p>
    </div>

    <div class="section-heading">
      <h2>社区动态</h2>
      <button type="button" @click="openPublishModal">发布</button>
    </div>

    <div v-if="postError && !feedItems.length" class="empty-state">{{ postError }}</div>
    <div v-else-if="loadingPosts && !feedItems.length" class="empty-state">正在从后端加载动态...</div>
    <div v-else class="feed-list">
      <article v-for="item in feedItems" :key="item.id" class="feed-item">
        <div class="avatar small" :class="item.avatarColor">{{ item.avatar }}</div>
        <div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
          <span>{{ item.meta }}</span>
        </div>
      </article>
    </div>

    <ResultModal :visible="publishVisible" @close="closePublishModal">
      <div class="result-heading">
        <p>发布新动态</p>
        <h2 id="modal-title">记录一条新的校园动态</h2>
      </div>
      <form class="archive-form" @submit.prevent="submitPost">
        <label>
          动态类型
          <select v-model="publishForm.type">
            <option value="checkin">日常打卡</option>
            <option value="feed">投喂记录</option>
            <option value="rescue">救助求助</option>
          </select>
        </label>
        <label>
          关联动物
          <select v-model="publishForm.animalId">
            <option value="">不关联具体动物</option>
            <option v-for="animal in animalOptions" :key="animal.value" :value="animal.value">
              {{ animal.label }}
            </option>
          </select>
        </label>
        <label>
          发布位置
          <input v-model="publishForm.location" required maxlength="24" placeholder="例如：北区食堂">
        </label>
        <label>
          动态内容
          <textarea v-model="publishForm.content" required maxlength="120" placeholder="记录你看到的情况、投喂内容或救助进展"></textarea>
        </label>
        <div v-if="publishError" class="auth-error">{{ publishError }}</div>
        <button class="solid-button" :disabled="posting" type="submit">
          {{ posting ? "发布中..." : "立即发布" }}
        </button>
      </form>
    </ResultModal>
  </section>
</template>
