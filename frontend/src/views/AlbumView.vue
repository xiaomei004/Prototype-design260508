<script setup>
import { onMounted } from "vue";
import { storeToRefs } from "pinia";
import AnimalCard from "../components/AnimalCard.vue";
import { useAnimalStore } from "../stores/animal";

const animalStore = useAnimalStore();
const { currentFilter, searchKeyword, visibleAnimals, loadingAnimals, animalError } = storeToRefs(animalStore);

const filterOptions = ["全部", "猫", "狗"];
const mapPins = [
  { name: "大橘", className: "pin-one" },
  { name: "学霸狗", className: "pin-two" },
  { name: "三花", className: "pin-three" },
  { name: "小黄", className: "pin-four" }
];

onMounted(async () => {
  try {
    await animalStore.fetchAnimals();
  } catch {
    // The view displays the backend error state.
  }
});
</script>

<template>
  <section class="page active" id="album-page">
    <div class="album-layout">
      <aside class="album-sidebar">
        <div class="toolbar">
          <label class="search-box">
            <span></span>
            <input
              :value="searchKeyword"
              type="search"
              placeholder="搜索名称、位置或标签"
              @input="animalStore.setSearchKeyword($event.target.value)"
            >
          </label>
          <div class="segmented" role="group" aria-label="动物种类筛选">
            <button
              v-for="option in filterOptions"
              :key="option"
              :class="{ active: currentFilter === option }"
              type="button"
              @click="animalStore.setFilter(option)"
            >
              {{ option }}
            </button>
          </div>
        </div>

        <div class="map-panel">
          <div class="campus-map">
            <span v-for="pin in mapPins" :key="pin.name" class="map-pin" :class="pin.className">{{ pin.name }}</span>
          </div>
          <div>
            <h2>湖工大活动热区</h2>
            <p>根据最近打卡位置模拟生成，点击图鉴卡片可用于演示档案查看。</p>
          </div>
        </div>
      </aside>

      <div v-if="animalError" class="empty-state">{{ animalError }}</div>
      <div v-else-if="loadingAnimals && !visibleAnimals.length" class="empty-state">正在从后端加载动物档案...</div>
      <div v-else class="animal-grid">
        <AnimalCard v-for="animal in visibleAnimals" :key="animal.id" :animal="animal" />
        <div v-if="!visibleAnimals.length" class="empty-state">没有找到符合条件的档案</div>
      </div>
    </div>
  </section>
</template>
