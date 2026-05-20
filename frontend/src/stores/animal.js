import { computed, ref } from "vue";
import { defineStore } from "pinia";
import { createAnimal, getAnimalList } from "../api/animal";
import { createPost, getPostList } from "../api/post";
import { resolveBackendAssetUrl } from "../utils/url";

function fallbackImage(species) {
  return species === "狗"
    ? new URL("../assets/images/dog2.png", import.meta.url).href
    : new URL("../assets/images/cat1.png", import.meta.url).href;
}

function resolveAnimalImage(image, species) {
  return resolveBackendAssetUrl(image) || fallbackImage(species);
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

  const diffDays = Math.round(diffHours / 24);
  return `${diffDays} 天前`;
}

export const useAnimalStore = defineStore("animal", () => {
  const animals = ref([]);
  const feedItems = ref([]);
  const currentFilter = ref("全部");
  const searchKeyword = ref("");
  const loadingAnimals = ref(false);
  const loadingPosts = ref(false);
  const posting = ref(false);
  const usingRemoteAnimals = ref(true);
  const usingRemotePosts = ref(true);
  const animalError = ref("");
  const postError = ref("");

  const animalCount = computed(() => animals.value.length);
  const createdCount = computed(() => animals.value.length);
  const visibleAnimals = computed(() => {
    const normalized = searchKeyword.value.trim().toLowerCase();
    return animals.value.filter((animal) => {
      const speciesMatched = currentFilter.value === "全部" || animal.species === currentFilter.value;
      const text = `${animal.name} ${animal.location} ${animal.status} ${animal.features || ""}`.toLowerCase();
      return speciesMatched && text.includes(normalized);
    });
  });

  function setFilter(filter) {
    currentFilter.value = filter;
  }

  function setSearchKeyword(keyword) {
    searchKeyword.value = keyword;
  }

  function normalizeRemoteAnimal(animal) {
    return {
      ...animal,
      image: resolveAnimalImage(animal.image || animal.imageUrl, animal.species)
    };
  }

  async function fetchAnimals(params = {}) {
    loadingAnimals.value = true;
    animalError.value = "";
    try {
      const data = await getAnimalList({
        keyword: params.keyword ?? searchKeyword.value,
        species: params.species && params.species !== "全部" ? params.species : undefined,
        status: params.status,
        pageNum: 1,
        pageSize: 100
      });
      animals.value = (data.list || []).map(normalizeRemoteAnimal);
      usingRemoteAnimals.value = true;
      return animals.value;
    } catch (error) {
      animals.value = [];
      usingRemoteAnimals.value = false;
      animalError.value = "无法连接后端动物档案服务，请先启动后端和 MySQL。";
      throw error;
    } finally {
      loadingAnimals.value = false;
    }
  }

  function toFeedItem(post) {
    return {
      id: post.id,
      avatar: (post.nickname || "用户").slice(0, 1),
      avatarColor: post.type === "rescue" ? "green" : "orange",
      title: post.animalName
        ? `${post.nickname}在${post.location || "校园"}发布了“${post.animalName}”相关动态`
        : `${post.nickname}发布了校园动态`,
      description: post.content,
      meta: `${post.location || "校园"} · ${formatRelativeLabel(post.createTime)}`
    };
  }

  async function fetchFeedItems() {
    loadingPosts.value = true;
    postError.value = "";
    try {
      const data = await getPostList({
        pageNum: 1,
        pageSize: 20
      });
      feedItems.value = (data.list || []).map(toFeedItem);
      usingRemotePosts.value = true;
      return feedItems.value;
    } catch (error) {
      feedItems.value = [];
      usingRemotePosts.value = false;
      postError.value = "无法连接后端动态服务，请先启动后端和 MySQL。";
      throw error;
    } finally {
      loadingPosts.value = false;
    }
  }

  async function addPost(payload) {
    posting.value = true;
    postError.value = "";
    try {
      const created = await createPost({
        animalId: payload.animalId || null,
        type: payload.type,
        content: payload.content.trim(),
        imageUrl: payload.imageUrl || null,
        location: payload.location.trim()
      });
      const newItem = toFeedItem(created);
      feedItems.value = [newItem, ...feedItems.value];
      usingRemotePosts.value = true;
      return created;
    } catch (error) {
      postError.value = error?.message || "发布动态失败，请稍后重试。";
      throw error;
    } finally {
      posting.value = false;
    }
  }

  async function addAnimal(payload) {
    const created = await createAnimal({
      name: payload.name.trim(),
      species: payload.species,
      gender: payload.gender || "未知",
      color: payload.color || payload.species,
      location: payload.location.trim(),
      status: "待审核",
      features: payload.features.trim() || "新建档案",
      imageUrl: payload.imageUrl || payload.image || null,
      sterilized: 0,
      vaccineStatus: "未知",
      firstFoundTime: new Date().toISOString().slice(0, 19)
    });
    const normalizedAnimal = normalizeRemoteAnimal(created);
    animals.value = [normalizedAnimal, ...animals.value.filter((item) => item.id !== normalizedAnimal.id)];
    usingRemoteAnimals.value = true;
    return normalizedAnimal;
  }

  return {
    animals,
    feedItems,
    currentFilter,
    searchKeyword,
    animalCount,
    createdCount,
    visibleAnimals,
    loadingAnimals,
    loadingPosts,
    posting,
    usingRemoteAnimals,
    usingRemotePosts,
    animalError,
    postError,
    setFilter,
    setSearchKeyword,
    fetchAnimals,
    fetchFeedItems,
    addPost,
    addAnimal
  };
});
