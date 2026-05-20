<script setup>
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getAnimalDetail } from "../api/animal";
import { getPostList } from "../api/post";
import { resolveBackendAssetUrl } from "../utils/url";

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const loadingPosts = ref(false);
const errorMessage = ref("");
const animal = ref(null);
const relatedPosts = ref([]);

const detailRows = computed(() => {
  if (!animal.value) {
    return [];
  }

  return [
    { label: "物种", value: animal.value.species || "未知" },
    { label: "性别", value: animal.value.gender || "未知" },
    { label: "毛色", value: animal.value.color || "未知" },
    { label: "发现位置", value: animal.value.location || "未知" },
    { label: "健康状态", value: animal.value.status || "未知" },
    { label: "绝育情况", value: animal.value.sterilized ? "已绝育" : "未绝育" },
    { label: "疫苗状态", value: animal.value.vaccineStatus || "未知" },
    { label: "审核状态", value: auditStatusText(animal.value.auditStatus) },
    { label: "首次发现", value: formatDateTime(animal.value.firstFoundTime) },
    { label: "建档时间", value: formatDateTime(animal.value.createTime) }
  ];
});

const coverUrl = computed(() => {
  if (!animal.value) {
    return new URL("../assets/images/cat1.png", import.meta.url).href;
  }

  return resolveBackendAssetUrl(animal.value.imageUrl)
    || new URL("../assets/images/cat1.png", import.meta.url).href;
});

function auditStatusText(status) {
  if (status === "APPROVED") {
    return "已通过";
  }
  if (status === "REJECTED") {
    return "已驳回";
  }
  return "待审核";
}

function formatDateTime(value) {
  if (!value) {
    return "未记录";
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")} ${String(date.getHours()).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}`;
}

function formatRelativeLabel(dateText) {
  if (!dateText) {
    return "刚刚";
  }

  const date = new Date(dateText);
  if (Number.isNaN(date.getTime())) {
    return dateText;
  }

  const diffMinutes = Math.max(1, Math.round((Date.now() - date.getTime()) / 60000));
  if (diffMinutes < 60) {
    return `${diffMinutes} 分钟前`;
  }

  const diffHours = Math.round(diffMinutes / 60);
  if (diffHours < 24) {
    return `${diffHours} 小时前`;
  }

  return `${Math.round(diffHours / 24)} 天前`;
}

async function loadRelatedPosts(animalId) {
  loadingPosts.value = true;
  try {
    const data = await getPostList({
      animalId,
      pageNum: 1,
      pageSize: 20
    });
    relatedPosts.value = data.list || [];
  } catch {
    relatedPosts.value = [];
  } finally {
    loadingPosts.value = false;
  }
}

async function loadDetail() {
  loading.value = true;
  errorMessage.value = "";
  try {
    animal.value = await getAnimalDetail(route.params.id);
    await loadRelatedPosts(route.params.id);
  } catch (error) {
    errorMessage.value = error?.message || "加载动物档案详情失败。";
  } finally {
    loading.value = false;
  }
}

onMounted(loadDetail);
</script>

<template>
  <section class="page active" id="animal-detail-page">
    <div class="detail-header">
      <button class="outline-button detail-back" type="button" @click="router.back()">返回</button>
      <button class="outline-button detail-back" type="button" @click="router.push({ name: 'album' })">回到图鉴</button>
    </div>

    <div v-if="errorMessage" class="empty-state">{{ errorMessage }}</div>
    <div v-else-if="loading" class="empty-state">正在加载动物档案详情...</div>
    <div v-else-if="animal" class="animal-detail-layout">
      <article class="animal-detail-hero">
        <img class="animal-detail-image" :src="coverUrl" :alt="animal.name">
        <div class="animal-detail-copy">
          <p class="eyebrow">校园图鉴详情</p>
          <div class="animal-title-row">
            <h2>{{ animal.name }}</h2>
            <span
              class="badge"
              :class="animal.auditStatus === 'APPROVED' ? 'green' : animal.auditStatus === 'REJECTED' ? 'red' : 'gray'"
            >
              {{ auditStatusText(animal.auditStatus) }}
            </span>
          </div>
          <p class="animal-detail-summary">{{ animal.features || "暂无特征描述" }}</p>
          <div class="tag-row">
            <span>{{ animal.status }}</span>
            <span>{{ animal.species }}</span>
            <span>{{ animal.sterilized ? "已绝育" : "未绝育" }}</span>
          </div>
        </div>
      </article>

      <article class="animal-detail-panel">
        <div class="section-heading">
          <h2>档案信息</h2>
        </div>
        <div class="detail-grid">
          <div v-for="row in detailRows" :key="row.label" class="detail-row">
            <span>{{ row.label }}</span>
            <strong>{{ row.value }}</strong>
          </div>
        </div>
      </article>

      <article class="animal-detail-panel related-posts-panel">
        <div class="section-heading">
          <h2>相关动态</h2>
        </div>
        <div v-if="loadingPosts" class="empty-state">正在加载相关动态...</div>
        <div v-else-if="!relatedPosts.length" class="empty-state">当前还没有关联到这只动物的动态</div>
        <div v-else class="feed-list">
          <article v-for="post in relatedPosts" :key="post.id" class="feed-item">
            <div class="avatar small orange">{{ (post.nickname || "用户").slice(0, 1) }}</div>
            <div>
              <h3>{{ post.nickname }}发布了相关动态</h3>
              <p>{{ post.content }}</p>
              <span>{{ post.location || "校园" }} · {{ formatRelativeLabel(post.createTime) }}</span>
            </div>
          </article>
        </div>
      </article>
    </div>
  </section>
</template>
