<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";

const props = defineProps({
  animal: {
    type: Object,
    required: true
  },
  clickable: {
    type: Boolean,
    default: true
  }
});

const router = useRouter();

const tagList = computed(() =>
  (props.animal.features || "")
    .split(",")
    .map((tag) => tag.trim())
    .filter(Boolean)
);

const badgeClass = computed(() => {
  const status = props.animal.status || "";
  if (status.includes("已绝育") || status.includes("健康")) {
    return "green";
  }
  if (status.includes("受伤") || status.includes("求助")) {
    return "red";
  }
  return "gray";
});

function openDetail() {
  if (!props.clickable || !props.animal.id) {
    return;
  }

  router.push({
    name: "animal-detail",
    params: { id: props.animal.id }
  });
}
</script>

<template>
  <article
    class="animal-card"
    :class="{ clickable }"
    @click="openDetail"
  >
    <img class="animal-portrait" :src="animal.image" :alt="`${animal.name}的档案照片`">
    <div class="animal-body">
      <div class="animal-title-row">
        <div>
          <h3>{{ animal.name }}</h3>
          <p>{{ animal.species }} · {{ animal.location }}</p>
        </div>
        <span class="badge" :class="badgeClass">{{ animal.status }}</span>
      </div>
      <div class="tag-row">
        <span v-for="tag in tagList" :key="tag">{{ tag }}</span>
      </div>
    </div>
  </article>
</template>
