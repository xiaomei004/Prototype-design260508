<script setup>
import { computed, onMounted, ref } from "vue";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";
import { auditAnimal, deleteAnimal, getPendingAnimals, updateAnimal } from "../api/animal";
import { deletePost, updatePost } from "../api/post";
import { getMyAnimals, getMyPosts, getMyScans } from "../api/user";
import ResultModal from "../components/ResultModal.vue";
import { useAnimalStore } from "../stores/animal";
import { useUserStore } from "../stores/user";
import { resolveBackendAssetUrl } from "../utils/url";

const router = useRouter();
const animalStore = useAnimalStore();
const userStore = useUserStore();
const { userInfo } = storeToRefs(userStore);

const myAnimals = ref([]);
const myPosts = ref([]);
const myScans = ref([]);
const profileLoading = ref(false);
const profileError = ref("");
const pendingAnimals = ref([]);
const reviewLoading = ref(false);
const reviewError = ref("");
const reviewingId = ref(null);

const editVisible = ref(false);
const deleteVisible = ref(false);
const editingAnimal = ref(null);
const deletingAnimal = ref(null);
const savingAnimal = ref(false);
const animalActionError = ref("");
const editForm = ref({
  location: "",
  status: "",
  features: ""
});

const postEditVisible = ref(false);
const postDeleteVisible = ref(false);
const editingPost = ref(null);
const deletingPost = ref(null);
const savingPost = ref(false);
const postActionError = ref("");
const postForm = ref({
  type: "checkin",
  location: "",
  content: ""
});

const isAdmin = computed(() => userInfo.value?.role === "ADMIN");
const myPostCount = computed(() => myPosts.value.length);
const myAnimalCount = computed(() => myAnimals.value.length);
const myScanCount = computed(() => myScans.value.length);

const settingItems = [
  {
    key: "footprint",
    title: "志愿足迹",
    description: "猫舍巡护、投喂记录"
  },
  {
    key: "message",
    title: "通知消息",
    description: "救助协同与审核提醒"
  },
  {
    key: "gear",
    title: "系统设置",
    description: "隐私、定位与消息偏好"
  }
];

function getAnimalCover(animal) {
  return resolveBackendAssetUrl(animal.imageUrl) || new URL("../assets/images/cat1.png", import.meta.url).href;
}

function getScanCover(scan) {
  return resolveBackendAssetUrl(scan.imageUrl) || new URL("../assets/images/cat1.png", import.meta.url).href;
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

function auditStatusText(status) {
  if (status === "APPROVED") {
    return "已通过";
  }
  if (status === "REJECTED") {
    return "已驳回";
  }
  return "待审核";
}

function scanResultText(resultType) {
  return resultType === "matched" ? "匹配成功" : "发现新成员";
}

async function loadProfileData() {
  profileLoading.value = true;
  profileError.value = "";
  try {
    const [animals, posts, scans] = await Promise.all([
      getMyAnimals(),
      getMyPosts(),
      getMyScans()
    ]);
    myAnimals.value = animals;
    myPosts.value = posts;
    myScans.value = scans;
  } catch (error) {
    profileError.value = error?.message || "加载个人数据失败。";
  } finally {
    profileLoading.value = false;
  }
}

async function loadPendingAnimals() {
  if (!isAdmin.value) {
    pendingAnimals.value = [];
    return;
  }

  reviewLoading.value = true;
  reviewError.value = "";
  try {
    pendingAnimals.value = await getPendingAnimals();
  } catch (error) {
    reviewError.value = error?.message || "加载待审核档案失败。";
  } finally {
    reviewLoading.value = false;
  }
}

async function refreshProfileModules() {
  await Promise.allSettled([
    animalStore.fetchAnimals(),
    animalStore.fetchFeedItems(),
    loadProfileData(),
    loadPendingAnimals()
  ]);
}

async function handleAudit(id, auditStatus) {
  reviewingId.value = id;
  reviewError.value = "";
  try {
    await auditAnimal(id, { auditStatus });
    pendingAnimals.value = pendingAnimals.value.filter((item) => item.id !== id);
    await refreshProfileModules();
  } catch (error) {
    reviewError.value = error?.message || "审核操作失败。";
  } finally {
    reviewingId.value = null;
  }
}

function openEditModal(animal) {
  editingAnimal.value = animal;
  editForm.value = {
    location: animal.location || "",
    status: animal.status || "",
    features: animal.features || ""
  };
  animalActionError.value = "";
  editVisible.value = true;
}

function closeEditModal() {
  editVisible.value = false;
  editingAnimal.value = null;
  animalActionError.value = "";
}

function openDeleteModal(animal) {
  deletingAnimal.value = animal;
  animalActionError.value = "";
  deleteVisible.value = true;
}

function closeDeleteModal() {
  deleteVisible.value = false;
  deletingAnimal.value = null;
  animalActionError.value = "";
}

async function submitAnimalEdit() {
  if (!editingAnimal.value) {
    return;
  }

  savingAnimal.value = true;
  animalActionError.value = "";
  try {
    await updateAnimal(editingAnimal.value.id, {
      location: editForm.value.location.trim(),
      status: editForm.value.status.trim(),
      features: editForm.value.features.trim()
    });
    closeEditModal();
    await refreshProfileModules();
  } catch (error) {
    animalActionError.value = error?.message || "更新档案失败。";
  } finally {
    savingAnimal.value = false;
  }
}

async function confirmDeleteAnimal() {
  if (!deletingAnimal.value) {
    return;
  }

  savingAnimal.value = true;
  animalActionError.value = "";
  try {
    await deleteAnimal(deletingAnimal.value.id);
    closeDeleteModal();
    await refreshProfileModules();
  } catch (error) {
    animalActionError.value = error?.message || "删除档案失败。";
  } finally {
    savingAnimal.value = false;
  }
}

function openPostEditModal(post) {
  editingPost.value = post;
  postForm.value = {
    type: post.type || "checkin",
    location: post.location || "",
    content: post.content || ""
  };
  postActionError.value = "";
  postEditVisible.value = true;
}

function closePostEditModal() {
  postEditVisible.value = false;
  editingPost.value = null;
  postActionError.value = "";
}

function openPostDeleteModal(post) {
  deletingPost.value = post;
  postActionError.value = "";
  postDeleteVisible.value = true;
}

function closePostDeleteModal() {
  postDeleteVisible.value = false;
  deletingPost.value = null;
  postActionError.value = "";
}

async function submitPostEdit() {
  if (!editingPost.value) {
    return;
  }

  savingPost.value = true;
  postActionError.value = "";
  try {
    await updatePost(editingPost.value.id, {
      animalId: editingPost.value.animalId || null,
      type: postForm.value.type,
      location: postForm.value.location.trim(),
      content: postForm.value.content.trim()
    });
    closePostEditModal();
    await refreshProfileModules();
  } catch (error) {
    postActionError.value = error?.message || "更新动态失败。";
  } finally {
    savingPost.value = false;
  }
}

async function confirmDeletePost() {
  if (!deletingPost.value) {
    return;
  }

  savingPost.value = true;
  postActionError.value = "";
  try {
    await deletePost(deletingPost.value.id);
    closePostDeleteModal();
    await refreshProfileModules();
  } catch (error) {
    postActionError.value = error?.message || "删除动态失败。";
  } finally {
    savingPost.value = false;
  }
}

function goToAnimalDetail(id) {
  router.push({
    name: "animal-detail",
    params: { id }
  });
}

function logout() {
  userStore.logout();
  router.replace({ name: "auth" });
}

onMounted(async () => {
  await Promise.allSettled([
    loadProfileData(),
    loadPendingAnimals()
  ]);
});
</script>

<template>
  <section class="page active" id="profile-page">
    <div class="profile-card">
      <div class="profile-photo" :aria-label="`${userInfo?.nickname || '用户'}头像`"></div>
      <div>
        <p>{{ isAdmin ? "动保协会管理员" : "校园志愿者" }}</p>
        <h2>{{ userInfo?.nickname || "未登录" }}</h2>
        <span>{{ userInfo?.username || "请先登录后查看个人信息" }}</span>
      </div>
    </div>

    <div class="profile-metrics">
      <article><strong>{{ myPostCount }}</strong><span>我的发布</span></article>
      <article><strong>{{ myAnimalCount }}</strong><span>我建档的</span></article>
      <article><strong>{{ myScanCount }}</strong><span>我的识别</span></article>
    </div>

    <div class="profile-posts-panel">
      <div class="section-heading">
        <h2>我的发布</h2>
        <button type="button" @click="loadProfileData">刷新</button>
      </div>

      <div v-if="profileError" class="empty-state">{{ profileError }}</div>
      <div v-else-if="profileLoading" class="empty-state">正在加载个人动态...</div>
      <div v-else-if="!myPosts.length" class="empty-state">你还没有发布过动态</div>
      <div v-else class="feed-list">
        <article v-for="post in myPosts" :key="post.id" class="feed-item">
          <div class="avatar small orange">{{ (userInfo?.nickname || '我').slice(0, 1) }}</div>
          <div>
            <h3>{{ post.animalName ? `我发布了“${post.animalName}”相关动态` : "我发布了一条校园动态" }}</h3>
            <p>{{ post.content }}</p>
            <span>{{ post.location || "校园" }} · {{ formatRelativeLabel(post.createTime) }}</span>
            <div class="profile-post-actions">
              <button class="outline-button" type="button" @click="openPostEditModal(post)">编辑</button>
              <button class="outline-button danger-button" type="button" @click="openPostDeleteModal(post)">删除</button>
            </div>
          </div>
        </article>
      </div>
    </div>

    <div class="profile-scans-panel">
      <div class="section-heading">
        <h2>我的扫描记录</h2>
        <button type="button" @click="loadProfileData">刷新</button>
      </div>

      <div v-if="profileError" class="empty-state">{{ profileError }}</div>
      <div v-else-if="profileLoading" class="empty-state">正在加载扫描记录...</div>
      <div v-else-if="!myScans.length" class="empty-state">你还没有扫描过动物图片</div>
      <div v-else class="profile-scan-list">
        <article v-for="scan in myScans" :key="scan.id" class="profile-scan-card">
          <img class="profile-scan-image" :src="getScanCover(scan)" alt="扫描记录图片">
          <div class="profile-scan-copy">
            <div class="animal-title-row">
              <h3>{{ scanResultText(scan.resultType) }}</h3>
              <span class="badge" :class="scan.resultType === 'matched' ? 'green' : 'gray'">
                {{ scan.resultType === "matched" ? "已匹配" : "待建档" }}
              </span>
            </div>
            <p v-if="scan.resultType === 'matched' && scan.matchedAnimalName">
              匹配对象：{{ scan.matchedAnimalName }} · 匹配度 {{ scan.similarity || 0 }}%
            </p>
            <p v-else>
              系统判断为新成员，可继续补充建档流程。
            </p>
            <span>{{ formatRelativeLabel(scan.createTime) }}</span>
          </div>
        </article>
      </div>
    </div>

    <div class="profile-animals-panel">
      <div class="section-heading">
        <h2>我建档的</h2>
        <button type="button" @click="loadProfileData">刷新</button>
      </div>

      <div v-if="profileError" class="empty-state">{{ profileError }}</div>
      <div v-else-if="profileLoading" class="empty-state">正在加载我的档案...</div>
      <div v-else-if="!myAnimals.length" class="empty-state">你还没有创建过动物档案</div>
      <div v-else class="profile-animal-list">
        <article v-for="animal in myAnimals" :key="animal.id" class="profile-animal-card">
          <img class="profile-animal-image" :src="getAnimalCover(animal)" :alt="animal.name">
          <div class="profile-animal-copy">
            <div class="animal-title-row">
              <h3>{{ animal.name }}</h3>
              <span class="badge" :class="animal.auditStatus === 'APPROVED' ? 'green' : animal.auditStatus === 'REJECTED' ? 'red' : 'gray'">
                {{ auditStatusText(animal.auditStatus) }}
              </span>
            </div>
            <p>{{ animal.species }} · {{ animal.location }}</p>
            <p>{{ animal.features || "暂无特征说明" }}</p>
            <div class="tag-row">
              <span>{{ animal.status }}</span>
              <span>{{ animal.sterilized ? "已绝育" : "未绝育" }}</span>
            </div>
            <div class="profile-animal-actions">
              <button class="outline-button" type="button" @click="goToAnimalDetail(animal.id)">查看详情</button>
              <button class="outline-button" type="button" @click="openEditModal(animal)">编辑</button>
              <button class="outline-button danger-button" type="button" @click="openDeleteModal(animal)">删除</button>
            </div>
          </div>
        </article>
      </div>
    </div>

    <div v-if="isAdmin" class="admin-review-panel">
      <div class="section-heading">
        <h2>管理员审核区</h2>
        <button type="button" @click="loadPendingAnimals">刷新</button>
      </div>

      <div v-if="reviewError" class="empty-state">{{ reviewError }}</div>
      <div v-else-if="reviewLoading" class="empty-state">正在加载待审核档案...</div>
      <div v-else-if="!pendingAnimals.length" class="empty-state">当前没有待审核档案</div>
      <div v-else class="admin-review-list">
        <article v-for="animal in pendingAnimals" :key="animal.id" class="admin-review-card">
          <img class="admin-review-image" :src="getAnimalCover(animal)" :alt="animal.name">
          <div class="admin-review-copy">
            <div class="animal-title-row">
              <h3>{{ animal.name }}</h3>
              <span class="badge red">待审核</span>
            </div>
            <p>{{ animal.species }} · {{ animal.location }}</p>
            <p>{{ animal.features || "暂无特征说明" }}</p>
            <div class="admin-review-actions">
              <button
                class="solid-button"
                type="button"
                :disabled="reviewingId === animal.id"
                @click="handleAudit(animal.id, 'APPROVED')"
              >
                {{ reviewingId === animal.id ? "处理中..." : "通过" }}
              </button>
              <button
                class="outline-button"
                type="button"
                :disabled="reviewingId === animal.id"
                @click="handleAudit(animal.id, 'REJECTED')"
              >
                驳回
              </button>
            </div>
          </div>
        </article>
      </div>
    </div>

    <div class="settings-list">
      <button v-for="item in settingItems" :key="item.key" type="button">
        <span class="setting-icon" :class="item.key"></span>
        <span class="setting-copy"><b>{{ item.title }}</b><i>{{ item.description }}</i></span>
        <span class="setting-arrow"></span>
      </button>
      <button type="button" @click="logout">
        <span class="setting-icon gear"></span>
        <span class="setting-copy"><b>退出登录</b><i>清除当前会话并返回登录页</i></span>
        <span class="setting-arrow"></span>
      </button>
    </div>

    <ResultModal :visible="editVisible" @close="closeEditModal">
      <div class="result-heading">
        <p>编辑动物档案</p>
        <h2 id="modal-title">更新“{{ editingAnimal?.name || '当前档案' }}”的信息</h2>
      </div>
      <form class="archive-form" @submit.prevent="submitAnimalEdit">
        <label>
          发现位置
          <input v-model="editForm.location" required maxlength="24" placeholder="例如：北区食堂">
        </label>
        <label>
          当前状态
          <input v-model="editForm.status" required maxlength="24" placeholder="例如：健康、待观察">
        </label>
        <label>
          特征说明
          <input v-model="editForm.features" maxlength="48" placeholder="例如：亲人、怕生、活泼">
        </label>
        <div v-if="animalActionError" class="auth-error">{{ animalActionError }}</div>
        <button class="solid-button" :disabled="savingAnimal" type="submit">
          {{ savingAnimal ? "保存中..." : "保存修改" }}
        </button>
      </form>
    </ResultModal>

    <ResultModal :visible="deleteVisible" @close="closeDeleteModal">
      <div class="result-heading">
        <p>删除动物档案</p>
        <h2 id="modal-title">确认删除“{{ deletingAnimal?.name || '当前档案' }}”吗？</h2>
      </div>
      <p class="animal-delete-copy">删除后将无法从“我建档的”中恢复，请确认这是你要执行的操作。</p>
      <div v-if="animalActionError" class="auth-error">{{ animalActionError }}</div>
      <div class="result-actions">
        <button class="outline-button" :disabled="savingAnimal" type="button" @click="closeDeleteModal">取消</button>
        <button class="solid-button danger-solid-button" :disabled="savingAnimal" type="button" @click="confirmDeleteAnimal">
          {{ savingAnimal ? "删除中..." : "确认删除" }}
        </button>
      </div>
    </ResultModal>

    <ResultModal :visible="postEditVisible" @close="closePostEditModal">
      <div class="result-heading">
        <p>编辑动态</p>
        <h2 id="modal-title">更新这条已发布的动态</h2>
      </div>
      <form class="archive-form" @submit.prevent="submitPostEdit">
        <label>
          动态类型
          <select v-model="postForm.type">
            <option value="checkin">日常打卡</option>
            <option value="feed">投喂记录</option>
            <option value="rescue">救助求助</option>
          </select>
        </label>
        <label>
          发布位置
          <input v-model="postForm.location" required maxlength="24" placeholder="例如：北区食堂">
        </label>
        <label>
          动态内容
          <textarea v-model="postForm.content" required maxlength="120" placeholder="更新动态说明"></textarea>
        </label>
        <div v-if="postActionError" class="auth-error">{{ postActionError }}</div>
        <button class="solid-button" :disabled="savingPost" type="submit">
          {{ savingPost ? "保存中..." : "保存修改" }}
        </button>
      </form>
    </ResultModal>

    <ResultModal :visible="postDeleteVisible" @close="closePostDeleteModal">
      <div class="result-heading">
        <p>删除动态</p>
        <h2 id="modal-title">确认删除这条动态吗？</h2>
      </div>
      <p class="animal-delete-copy">删除后这条动态将不会再出现在发现广场和个人中心。</p>
      <div v-if="postActionError" class="auth-error">{{ postActionError }}</div>
      <div class="result-actions">
        <button class="outline-button" :disabled="savingPost" type="button" @click="closePostDeleteModal">取消</button>
        <button class="solid-button danger-solid-button" :disabled="savingPost" type="button" @click="confirmDeletePost">
          {{ savingPost ? "删除中..." : "确认删除" }}
        </button>
      </div>
    </ResultModal>
  </section>
</template>
