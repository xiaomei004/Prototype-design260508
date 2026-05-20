<script setup>
import { computed, onBeforeUnmount, ref } from "vue";
import { useRouter } from "vue-router";
import { scanAnimal } from "../api/scan";
import { uploadFile } from "../api/upload";
import AnimalCard from "../components/AnimalCard.vue";
import ResultModal from "../components/ResultModal.vue";
import { useAnimalStore } from "../stores/animal";

const router = useRouter();
const animalStore = useAnimalStore();

const scanStep = ref(1);
const previewVisible = ref(false);
const modalVisible = ref(false);
const resultType = ref("");
const scanTimer = ref(null);
const selectedImageUrl = ref("");
const selectedFileName = ref("");
const scanning = ref(false);
const scanError = ref("");
const fileInput = ref(null);
const matchedAnimalData = ref(null);
const form = ref({
  name: "",
  species: "猫",
  location: "",
  features: ""
});

const matchedAnimal = computed(() => matchedAnimalData.value || animalStore.animals[0] || null);

function setScanStep(step) {
  scanStep.value = step;
}

function resetScan() {
  window.clearTimeout(scanTimer.value);
  scanTimer.value = null;
  scanStep.value = 1;
  previewVisible.value = false;
  modalVisible.value = false;
  resultType.value = "";
  selectedImageUrl.value = "";
  selectedFileName.value = "";
  scanning.value = false;
  scanError.value = "";
  matchedAnimalData.value = null;
  form.value = {
    name: "",
    species: "猫",
    location: "",
    features: ""
  };
}

function openFilePicker() {
  fileInput.value?.click();
}

async function startScan(imageUrl) {
  setScanStep(2);
  previewVisible.value = true;
  scanning.value = true;
  scanError.value = "";
  selectedImageUrl.value = imageUrl;
  window.clearTimeout(scanTimer.value);
  scanTimer.value = window.setTimeout(async () => {
    try {
      const result = await scanAnimal({ imageUrl });
      matchedAnimalData.value = result.animal || null;
      await animalStore.fetchAnimals();
      setScanStep(3);
      resultType.value = result.resultType;
      modalVisible.value = true;
    } catch (error) {
      scanError.value = "识别失败，请确认后端上传和扫描接口已正常启动。";
      previewVisible.value = false;
      setScanStep(1);
    } finally {
      scanning.value = false;
    }
  }, 1200);
}

async function handleFileChange(event) {
  const file = event.target.files?.[0];
  if (!file) {
    return;
  }

  selectedFileName.value = file.name;
  scanError.value = "";
  try {
    const uploadResult = await uploadFile(file);
    await startScan(uploadResult.url);
  } catch (error) {
    scanError.value = "上传失败，请确认后端文件上传服务已正常启动。";
  } finally {
    event.target.value = "";
  }
}

function closeModal() {
  resetScan();
}

function goToDiscover() {
  closeModal();
  router.push({ name: "discover" });
}

function goToAlbum() {
  closeModal();
  router.push({ name: "album" });
}

async function submitNewAnimal() {
  try {
    await animalStore.addAnimal({
      ...form.value,
      imageUrl: selectedImageUrl.value
    });
    goToAlbum();
  } catch (error) {
    scanError.value = "新成员建档失败，请确认后端动物档案服务已正常启动。";
  }
}

onBeforeUnmount(() => {
  window.clearTimeout(scanTimer.value);
});
</script>

<template>
  <section class="page active" id="camera-page">
    <div class="process-list" aria-label="AI 识别流程">
      <div :class="{ active: scanStep === 1, done: scanStep > 1 }"><span>1</span>上传图片</div>
      <div :class="{ active: scanStep === 2, done: scanStep > 2 }"><span>2</span>特征提取</div>
      <div :class="{ active: scanStep === 3 }"><span>3</span>档案匹配</div>
    </div>

    <div class="camera-stage">
      <input
        ref="fileInput"
        class="hidden-file-input"
        type="file"
        accept="image/*"
        @change="handleFileChange"
      >

      <button v-if="!previewVisible" class="upload-zone" type="button" @click="openFilePicker">
        <span class="camera-glyph"></span>
        <strong>点击拍照 / 上传</strong>
        <p>上传照片后将调用后端上传与识别接口，并匹配已有数字档案。</p>
        <span class="upload-cta">选择图片</span>
      </button>

      <div v-else class="scan-preview">
        <div class="preview-art cat-art" aria-hidden="true">
          <div class="ear left"></div>
          <div class="ear right"></div>
          <div class="face">
            <span></span>
            <span></span>
            <i></i>
          </div>
        </div>
        <div class="scan-mask">
          <p>{{ scanning ? "AI 正在提取动物特征..." : "识别完成，正在整理结果..." }}</p>
          <div class="scan-line"></div>
        </div>
      </div>

      <div v-if="selectedFileName" class="camera-hint">当前文件：{{ selectedFileName }}</div>
      <div v-if="scanError" class="empty-state">{{ scanError }}</div>
    </div>

    <ResultModal :visible="modalVisible" @close="closeModal">
      <template v-if="resultType === 'matched' && matchedAnimal">
        <div class="result-heading">
          <p>老朋友识别成功</p>
          <h2 id="modal-title">匹配成功！匹配度 92%</h2>
        </div>
        <AnimalCard :animal="matchedAnimal" />
        <div class="result-actions">
          <button class="solid-button" type="button" @click="goToDiscover">去打卡投喂</button>
          <button class="outline-button" type="button" @click="goToAlbum">查看图鉴</button>
        </div>
      </template>

      <template v-else-if="resultType === 'new'">
        <div class="result-heading">
          <p>新成员待建档</p>
          <h2 id="modal-title">系统提示：发现校园新物种！</h2>
        </div>
        <form class="archive-form" @submit.prevent="submitNewAnimal">
          <label>
            暂定名称
            <input v-model="form.name" required maxlength="12" placeholder="例如：小灰">
          </label>
          <label>
            种类
            <select v-model="form.species">
              <option value="猫">猫</option>
              <option value="狗">狗</option>
            </select>
          </label>
          <label>
            发现位置
            <input v-model="form.location" required maxlength="18" placeholder="例如：东区操场">
          </label>
          <label>
            特征标签
            <input v-model="form.features" maxlength="24" placeholder="例如：怕生，瘦小">
          </label>
          <button class="solid-button" type="submit">生成新档案</button>
        </form>
      </template>
    </ResultModal>
  </section>
</template>
