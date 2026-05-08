const STORAGE_KEY = "campusAnimalPrototypeList";

const initialAnimals = [
  {
    id: 1,
    name: "大橘",
    species: "猫",
    location: "北区食堂",
    status: "已绝育",
    features: "亲人, 贪吃",
    color: "cat",
    image: "cat1.png"
  },
  {
    id: 2,
    name: "学霸狗",
    species: "狗",
    location: "图书馆门口",
    status: "健康",
    features: "安静, 怕生",
    color: "dog",
    image: "dog1.png"
  },
  {
    id: 3,
    name: "三花",
    species: "猫",
    location: "南门草坪",
    status: "未绝育",
    features: "警惕, 爱晒太阳",
    color: "cat",
    image: "cat1.png"
  },
  {
    id: 4,
    name: "小黄",
    species: "狗",
    location: "体育馆侧门",
    status: "待观察",
    features: "活泼, 亲人",
    color: "dog",
    image: "dog2.png"
  }
];

let animalList = loadAnimals();
let currentFilter = "全部";
let searchKeyword = "";
let scanTimer = null;

const pageTitle = document.querySelector("#page-title");
const pages = document.querySelectorAll(".page");
const navButtons = document.querySelectorAll(".bottom-nav button");
const gotoButtons = document.querySelectorAll("[data-goto]");
const animalGrid = document.querySelector("#animal-grid");
const searchInput = document.querySelector("#search-input");
const filterButtons = document.querySelectorAll("[data-filter]");
const animalCount = document.querySelector("#animal-count");
const createdCount = document.querySelector("#created-count");
const startScanButton = document.querySelector("#start-scan");
const scanPreview = document.querySelector("#scan-preview");
const processSteps = document.querySelectorAll(".process-list div");
const modal = document.querySelector("#result-modal");
const modalContent = document.querySelector("#modal-content");
const closeModalButton = document.querySelector("#close-modal");

function loadAnimals() {
  const saved = localStorage.getItem(STORAGE_KEY);
  if (!saved) {
    return [...initialAnimals];
  }

  try {
    const parsed = JSON.parse(saved);
    return Array.isArray(parsed) && parsed.length ? normalizeAnimals(parsed) : [...initialAnimals];
  } catch {
    return [...initialAnimals];
  }
}

function normalizeAnimals(savedAnimals) {
  const enriched = savedAnimals.map((animal) => {
    const preset = initialAnimals.find((item) => item.name === animal.name);
    return {
      ...animal,
      image: animal.image || preset?.image || fallbackImage(animal)
    };
  });

  initialAnimals.forEach((animal) => {
    if (!enriched.some((item) => item.name === animal.name)) {
      enriched.push(animal);
    }
  });

  return enriched;
}

function saveAnimals() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(animalList));
}

function setActivePage(pageId) {
  pages.forEach((page) => {
    const isActive = page.id === pageId;
    page.classList.toggle("active", isActive);
    if (isActive) {
      pageTitle.textContent = page.dataset.title;
    }
  });

  navButtons.forEach((button) => {
    button.classList.toggle("active", button.dataset.target === pageId);
  });
}

function statusClass(status) {
  if (status.includes("已绝育") || status.includes("健康")) {
    return "green";
  }

  if (status.includes("受伤") || status.includes("求助")) {
    return "red";
  }

  return "gray";
}

function portraitText(animal) {
  if (animal.species === "狗") {
    return "DOG";
  }

  return "CAT";
}

function renderAnimalCard(animal) {
  const tags = animal.features
    .split(",")
    .map((tag) => tag.trim())
    .filter(Boolean)
    .map((tag) => `<span>${tag}</span>`)
    .join("");

  return `
    <article class="animal-card">
      <img class="animal-portrait" src="${animal.image || fallbackImage(animal)}" alt="${animal.name}的档案照片">
      <div class="animal-body">
        <div class="animal-title-row">
          <div>
            <h3>${animal.name}</h3>
            <p>${animal.species} · ${animal.location}</p>
          </div>
          <span class="badge ${statusClass(animal.status)}">${animal.status}</span>
        </div>
        <div class="tag-row">${tags}</div>
      </div>
    </article>
  `;
}

function fallbackImage(animal) {
  return animal.species === "狗" ? "dog2.png" : "cat1.png";
}

function renderAnimals() {
  const normalized = searchKeyword.trim().toLowerCase();
  const visibleAnimals = animalList.filter((animal) => {
    const speciesMatched = currentFilter === "全部" || animal.species === currentFilter;
    const text = `${animal.name} ${animal.location} ${animal.status} ${animal.features}`.toLowerCase();
    return speciesMatched && text.includes(normalized);
  });

  animalGrid.innerHTML = visibleAnimals.length
    ? visibleAnimals.map(renderAnimalCard).join("")
    : `<div class="empty-state">没有找到符合条件的档案</div>`;

  animalCount.textContent = animalList.length;
  createdCount.textContent = Math.max(1, animalList.length - initialAnimals.length + 1);
}

function resetScan() {
  clearTimeout(scanTimer);
  setScanStep(1);
  startScanButton.classList.remove("hidden");
  scanPreview.classList.add("hidden");
}

function setScanStep(step) {
  processSteps.forEach((item) => {
    const itemStep = Number(item.dataset.step);
    item.classList.toggle("active", itemStep === step);
    item.classList.toggle("done", itemStep < step);
  });
}

function startScan() {
  setScanStep(2);
  startScanButton.classList.add("hidden");
  scanPreview.classList.remove("hidden");

  clearTimeout(scanTimer);
  scanTimer = setTimeout(() => {
    setScanStep(3);
    Math.random() > 0.5 ? showMatchedResult() : showNewMemberForm();
  }, 2000);
}

function showModal(html) {
  modalContent.innerHTML = html;
  modal.classList.remove("hidden");
}

function closeModal() {
  modal.classList.add("hidden");
  modalContent.innerHTML = "";
  resetScan();
}

function showMatchedResult() {
  const orangeCat = animalList.find((animal) => animal.name === "大橘") || animalList[0];
  showModal(`
    <div class="result-heading">
      <p>老朋友识别成功</p>
      <h2 id="modal-title">匹配成功！匹配度 92%</h2>
    </div>
    ${renderAnimalCard(orangeCat)}
    <div class="result-actions">
      <button class="solid-button" type="button" id="feed-checkin">去打卡喂食</button>
      <button class="outline-button" type="button" id="view-album">查看图鉴</button>
    </div>
  `);

  document.querySelector("#feed-checkin").addEventListener("click", () => {
    closeModal();
    setActivePage("discover-page");
  });

  document.querySelector("#view-album").addEventListener("click", () => {
    closeModal();
    setActivePage("album-page");
  });
}

function showNewMemberForm() {
  showModal(`
    <div class="result-heading">
      <p>新成员待建档</p>
      <h2 id="modal-title">系统提示：发现校园新物种！</h2>
    </div>
    <form class="archive-form" id="archive-form">
      <label>
        暂定昵称
        <input name="name" required maxlength="12" placeholder="例如：小灰">
      </label>
      <label>
        种类
        <select name="species">
          <option value="猫">猫</option>
          <option value="狗">狗</option>
        </select>
      </label>
      <label>
        发现位置
        <input name="location" required maxlength="18" placeholder="例如：东区操场">
      </label>
      <label>
        特征标签
        <input name="features" maxlength="24" placeholder="例如：怕生, 瘦小">
      </label>
      <button class="solid-button" type="submit">生成新档案</button>
    </form>
  `);

  document.querySelector("#archive-form").addEventListener("submit", (event) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const species = formData.get("species");
    const newAnimal = {
      id: Date.now(),
      name: formData.get("name").trim(),
      species,
      location: formData.get("location").trim(),
      status: "待审核",
      features: formData.get("features").trim() || "新建档案",
      color: species === "狗" ? "dog" : "cat",
      image: species === "狗" ? "dog2.png" : "cat1.png"
    };

    animalList = [newAnimal, ...animalList];
    saveAnimals();
    renderAnimals();
    closeModal();
    setActivePage("album-page");
  });
}

function applyDemoMode() {
  const params = new URLSearchParams(window.location.search);
  const demo = params.get("demo");

  if (!demo) {
    return;
  }

  if (demo === "album") {
    setActivePage("album-page");
  }

  if (demo === "scan") {
    setActivePage("camera-page");
    setScanStep(2);
    startScanButton.classList.add("hidden");
    scanPreview.classList.remove("hidden");
  }

  if (demo === "matched") {
    setActivePage("camera-page");
    setScanStep(3);
    showMatchedResult();
  }

  if (demo === "new") {
    setActivePage("camera-page");
    setScanStep(3);
    showNewMemberForm();
  }
}

navButtons.forEach((button) => {
  button.addEventListener("click", () => setActivePage(button.dataset.target));
});

gotoButtons.forEach((button) => {
  button.addEventListener("click", () => setActivePage(button.dataset.goto));
});

filterButtons.forEach((button) => {
  button.addEventListener("click", () => {
    currentFilter = button.dataset.filter;
    filterButtons.forEach((item) => item.classList.toggle("active", item === button));
    renderAnimals();
  });
});

searchInput.addEventListener("input", (event) => {
  searchKeyword = event.target.value;
  renderAnimals();
});

startScanButton.addEventListener("click", startScan);
closeModalButton.addEventListener("click", closeModal);
modal.addEventListener("click", (event) => {
  if (event.target === modal) {
    closeModal();
  }
});

document.addEventListener("keydown", (event) => {
  if (event.key === "Escape" && !modal.classList.contains("hidden")) {
    closeModal();
  }
});

renderAnimals();
applyDemoMode();
